package me.hannes.eura_tasks.ui.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import me.hannes.eura_tasks.db.lists.DeletedUserListEntity
import me.hannes.eura_tasks.db.lists.ListDbDao
import me.hannes.eura_tasks.db.lists.UserListEntity
import me.hannes.eura_tasks.db.tasks.DeletedTasksEntity
import me.hannes.eura_tasks.db.tasks.TaskDbDao
import me.hannes.eura_tasks.db.tasks.TodoEntity
import java.util.Collections
import kotlin.time.Instant

data class TodoSyncModel(
    val uuid: String,
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val isCompleted: Boolean,
    val taskList: String,
    val date: String,
    val time: String,
    val creationTime: Instant
)

data class ListSyncModel(
    val title: String,
    val type: String,
    val color: String,
    val uuid: String
)

data class DeletedListSyncModel(
    val uuid: String,
    val deletionDate: Instant
)

class GoogleDriveViewModel(
    private val taskDao: TaskDbDao,
    private val listDao: ListDbDao,
) : ViewModel() {
    private var driveService: Drive? = null

    private val gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, com.google.gson.JsonSerializer<Instant> { src, _, _ ->
            com.google.gson.JsonPrimitive(src.toString())
        })
        .registerTypeAdapter(Instant::class.java, com.google.gson.JsonDeserializer { json, _, _ ->
            Instant.parse(json.asString)
        })
        .create()

    /**
     * Initializes the Drive service with the provided account.
     */
    fun initDriveService(context: Context, account: GoogleSignInAccount) {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, Collections.singleton(DriveScopes.DRIVE_FILE)
        ).setSelectedAccount(account.account)

        driveService = Drive.Builder(
            NetHttpTransport(),
            GsonFactory(),
            credential
        ).setApplicationName("eura-tasks").build()

        Log.d("eura-tasks", "Drive Service Ready for: ${account.email}")
    }

    /**
     * Checks if there is an existing Google sign-in and initializes the Drive service.
     */
    fun checkExistingLogin(context: Context) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            initDriveService(context, account)
        }
    }

    private suspend fun getOrCreateMainFolderId(): String? = withContext(Dispatchers.IO) {
        try {
            val mainFolderQuery = "name = 'eura-tasks' and mimeType = 'application/vnd.google-apps.folder' and trashed = false"
            val mainResult = driveService?.files()?.list()?.setQ(mainFolderQuery)?.execute()
            var mainFolderId = mainResult?.files?.firstOrNull()?.id

            if (mainFolderId == null) {
                val metadata = com.google.api.services.drive.model.File().apply {
                    name = "eura-tasks"
                    mimeType = "application/vnd.google-apps.folder"
                }
                mainFolderId = driveService?.files()?.create(metadata)?.setFields("id")?.execute()?.id
                Log.d("eura-tasks", "Created Main Folder: $mainFolderId")
            }
            return@withContext mainFolderId
        } catch (e: Exception) {
            Log.e("eura-tasks", "Main folder creation failed: ${e.message}")
            null
        }
    }

    private suspend fun getOrCreateSubFolderId(folderName: String, parentFolder: String?): String? =
        withContext(Dispatchers.IO) {
            try {
                val subFolderQuery =
                    "name = '$folderName' and '$parentFolder' in parents and mimeType = 'application/vnd.google-apps.folder' and trashed = false"
                val subResult = driveService?.files()?.list()?.setQ(subFolderQuery)?.execute()
                var subFolderId = subResult?.files?.firstOrNull()?.id

                if (subFolderId == null) {
                    val metadata = com.google.api.services.drive.model.File().apply {
                        name = folderName
                        mimeType = "application/vnd.google-apps.folder"
                        parents = listOf(parentFolder)
                    }
                    subFolderId = driveService?.files()?.create(metadata)?.setFields("id")?.execute()?.id
                    Log.d("eura-tasks", "Created Sub-Folder: $subFolderId")
                }
                return@withContext subFolderId
            } catch (e: Exception) {
                Log.e("eura-tasks", "Folder creation failed: ${e.message}")
                null
            }
        }

    private suspend fun <RemoteModel, LocalEntity> download(
        folderName: String,
        fileName: String,
        typeToken: java.lang.reflect.Type,
        transform: (RemoteModel) -> LocalEntity
    ): List<LocalEntity> = withContext(Dispatchers.IO) {
        if (driveService == null) {
            Log.e("eura-tasks", "Drive Service not initialized")
            return@withContext emptyList()
        }

        try {
            val folderId = getOrCreateSubFolderId(
                folderName = folderName,
                parentFolder = getOrCreateMainFolderId() ?: return@withContext emptyList()
            ) ?: return@withContext emptyList()

            val query = "name = '$fileName' and '$folderId' in parents and mimeType = 'application/json' and trashed = false"
            val existingFile = driveService?.files()?.list()?.setQ(query)?.execute()?.files?.firstOrNull()

            if (existingFile != null) {
                val outputStream = java.io.ByteArrayOutputStream()
                driveService?.files()?.get(existingFile.id)?.executeMediaAndDownloadTo(outputStream)

                val jsonString = outputStream.toString()
                val remoteItems = gson.fromJson<List<RemoteModel>>(jsonString, typeToken) ?: emptyList()
                return@withContext remoteItems.map(transform)
            }
        } catch (e: Exception) {
            Log.e("eura-tasks", "Failed to download cloud file $fileName: ${e.message}")
        }
        return@withContext emptyList()
    }

    private fun <RemoteModel, LocalEntity> sync(
        folderName: String,
        folderId: String,
        fileName: String,
        localData: List<LocalEntity>,
        transformToRemote: (LocalEntity) -> RemoteModel,
        downloadTypeToken: java.lang.reflect.Type,
        transformToLocal: (RemoteModel) -> LocalEntity,
        onComplete: (List<LocalEntity>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val remoteData = download(
                folderName = folderName,
                fileName = fileName,
                typeToken = downloadTypeToken,
                transform = transformToLocal
            )

            try {
                val syncLists = localData.map(transformToRemote)
                val jsonContent = gson.toJson(syncLists)
                val contentStream = ByteArrayContent.fromString("application/json", jsonContent)

                val query = "name = '$fileName' and '$folderId' in parents and trashed = false"
                val existingFile = driveService?.files()?.list()?.setQ(query)?.execute()?.files?.firstOrNull()

                if (existingFile != null) {
                    driveService?.files()?.update(existingFile.id, null, contentStream)?.execute()
                    Log.d("eura-tasks", "Updated $fileName on cloud storage.")
                } else {
                    val metadata = com.google.api.services.drive.model.File().apply {
                        name = fileName
                        parents = listOf(folderId)
                    }
                    driveService?.files()?.create(metadata, contentStream)?.execute()
                    Log.d("eura-tasks", "Created $fileName on cloud storage.")
                }
            } catch (e: Exception) {
                Log.e("eura-tasks", "Failed uploading $fileName back to cloud: ${e.message}")
            }

            withContext(Dispatchers.Main) {
                onComplete(remoteData)
            }
        }
    }

    /**
     * Synchronizes deleted user lists with Google Drive.
     */
    fun syncDeletedUserLists(onComplete: (List<DeletedUserListEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val mainFolderId = getOrCreateMainFolderId() ?: return@launch
            val folderId = getOrCreateSubFolderId("deleted_lists", mainFolderId) ?: return@launch

            val localDeletedLists = listDao.getAllDeletedLists().first()
            val typeToken = object : TypeToken<List<DeletedListSyncModel>>() {}.type

            sync(
                folderName = "deleted_lists",
                folderId = folderId,
                fileName = "deleted_lists.json",
                localData = localDeletedLists,
                transformToRemote = { entity ->
                    DeletedListSyncModel(uuid = entity.deletedUuid, deletionDate = entity.deletionDate)
                },
                downloadTypeToken = typeToken,
                transformToLocal = { model ->
                    DeletedUserListEntity(
                        deletedUuid = model.uuid,
                        deletionDate = model.deletionDate
                    )
                },
                onComplete = onComplete
            )
        }
    }

    /**
     * Synchronizes user lists with Google Drive.
     */
    fun syncUserLists(onComplete: (List<UserListEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val mainFolderId = getOrCreateMainFolderId() ?: return@launch
            val folderId = getOrCreateSubFolderId("lists", mainFolderId) ?: return@launch

            val localLists = listDao.getAllLists().first()
            val typeToken = object : TypeToken<List<ListSyncModel>>() {}.type

            sync(
                folderName = "lists",
                folderId = folderId,
                fileName = "lists.json",
                localData = localLists,
                transformToRemote = { entity ->
                    ListSyncModel(
                        uuid = entity.uuid,
                        title = entity.name,
                        color = entity.colorString,
                        type = entity.type
                    )
                },
                downloadTypeToken = typeToken,
                transformToLocal = { model ->
                    UserListEntity(
                        uuid = model.uuid,
                        name = model.title,
                        colorString = model.color,
                        type = model.type
                    )
                },
                onComplete = onComplete
            )
        }
    }

    /**
     * Synchronizes deleted tasks with Google Drive.
     */
    fun syncDeletedTasks(onComplete: (List<DeletedTasksEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val mainFolderId = getOrCreateMainFolderId() ?: return@launch
            val folderId = getOrCreateSubFolderId("deleted_tasks", mainFolderId) ?: return@launch

            val localDeletedTasks = taskDao.getAllDeletedTasks()
            val typeToken = object : TypeToken<List<DeletedListSyncModel>>() {}.type

            sync(
                folderName = "deleted_tasks",
                folderId = folderId,
                fileName = "deleted_tasks.json",
                localData = localDeletedTasks,
                transformToRemote = { entity ->
                    DeletedListSyncModel(uuid = entity.deletedUuid, deletionDate = entity.deletionDate)
                },
                downloadTypeToken = typeToken,
                transformToLocal = { model ->
                    DeletedTasksEntity(
                        deletedUuid = model.uuid,
                        deletionDate = model.deletionDate
                    )
                },
                onComplete = onComplete
            )
        }
    }

    /**
     * Downloads all tasks from Google Drive.
     */
    suspend fun downloadAllTasks(): List<TodoEntity> = withContext(Dispatchers.IO) {
        val downloadedTasks = mutableListOf<TodoEntity>()
        if (driveService == null) {
            Log.e("eura-tasks", "Drive Service not initialized")
            return@withContext emptyList()
        }

        try {
            val folderId = getOrCreateSubFolderId(
                folderName = "tasks",
                parentFolder = getOrCreateMainFolderId() ?: return@withContext emptyList()
            ) ?: return@withContext emptyList()

            val query = "'$folderId' in parents and mimeType = 'application/json' and trashed = false"
            val fileList = driveService?.files()?.list()?.setQ(query)?.execute()?.files ?: emptyList()

            fileList.forEach { file ->
                try {
                    val outputStream = java.io.ByteArrayOutputStream()
                    driveService?.files()?.get(file.id)?.executeMediaAndDownloadTo(outputStream)

                    val jsonString = outputStream.toString()
                    val syncModel = gson.fromJson(jsonString, TodoSyncModel::class.java)

                    downloadedTasks.add(
                        TodoEntity(
                            uuid = syncModel.uuid,
                            title = syncModel.title,
                            description = syncModel.description,
                            isFavorite = syncModel.isFavorite,
                            isCompleted = syncModel.isCompleted,
                            date = syncModel.date,
                            time = syncModel.time,
                            creationTime = syncModel.creationTime,
                            taskList = syncModel.taskList
                        )
                    )
                } catch (e: Exception) {
                    Log.e("eura-tasks", "Failed to download/parse file ${file.name}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("eura-tasks", "Download list failed: ${e.message}")
        }
        return@withContext downloadedTasks
    }

    /**
     * Deletes a task file from Google Drive.
     */
    fun deleteTaskFile(uuid: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            if (driveService == null) {
                Log.e("eura-tasks", "Drive Service not initialized")
                withContext(Dispatchers.Main) { onResult(false) }
                return@launch
            }

            try {
                val folderId = getOrCreateSubFolderId(
                    folderName = "tasks",
                    parentFolder = getOrCreateMainFolderId() ?: run {
                        withContext(Dispatchers.Main) { onResult(false) }
                        return@launch
                    }
                ) ?: run {
                    withContext(Dispatchers.Main) { onResult(false) }
                    return@launch
                }

                val fileName = "task_$uuid.json"
                val query = "name = '$fileName' and '$folderId' in parents and trashed = false"

                val existingFile = driveService?.files()?.list()?.setQ(query)?.execute()?.files?.firstOrNull()

                if (existingFile != null && existingFile.id != null) {
                    val trashedMetadata = com.google.api.services.drive.model.File().apply {
                        trashed = true
                    }
                    driveService?.files()?.update(existingFile.id, trashedMetadata)?.execute()
                    Log.d("eura-tasks", "Moved to trash: $fileName")
                    withContext(Dispatchers.Main) { onResult(true) }
                } else {
                    Log.w("eura-tasks", "Delete failed: File $fileName not found on Drive.")
                    withContext(Dispatchers.Main) { onResult(false) }
                }
            } catch (e: Exception) {
                Log.e("eura-tasks", "Error deleting file for UUID $uuid: ${e.message}")
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    /**
     * Triggers a complete sync of all data: lists, deleted lists, tasks, and deleted tasks.
     */
    fun startFullSync(
        taskDbViewModel: TaskDbViewModel,
        listDbViewModel: ListDbViewModel,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (driveService == null) {
                Log.e("eura-tasks", "Cannot sync: Drive Service not initialized")
                return@launch
            }

            Log.d("eura-tasks", "--- STARTING FULL SYNC ---")

            syncDeletedUserLists { cloudDeletedLists ->
                syncUserLists { cloudActiveLists ->
                    syncDeletedTasks { cloudDeletedTasks ->
                        viewModelScope.launch(Dispatchers.IO) {
                            val localDeletedListUuids =
                                listDao.getAllDeletedLists().first().map { it.deletedUuid }.toSet()
                            val cloudDeletedListUuids = cloudDeletedLists.map { it.deletedUuid }.toSet()
                            val allDeletedListUuids = localDeletedListUuids + cloudDeletedListUuids

                            val localActiveListUuids =
                                listDao.getAllLists().first().map { it.uuid }.toSet()

                            val cleanActiveLists = cloudActiveLists.filter { list ->
                                list.uuid !in allDeletedListUuids
                            }

                            cleanActiveLists.forEach { list ->
                                if (list.uuid !in localActiveListUuids) {
                                    listDbViewModel.insertList(
                                        name = list.name,
                                        color = list.colorString,
                                        type = list.type,
                                        uuid = list.uuid
                                    )
                                    Log.d("eura-tasks", "Downloaded new cloud list: ${list.name}")
                                }
                            }

                            if (cleanActiveLists.size < cloudActiveLists.size) {
                                Log.d("eura-tasks", "Detected zombie lists on cloud. Re-uploading cleaned lists.json to Drive.")

                                val listFolderId = getOrCreateSubFolderId("lists", getOrCreateMainFolderId())
                                if (listFolderId != null) {
                                    try {
                                        val syncLists = cleanActiveLists.map { entity ->
                                            ListSyncModel(
                                                uuid = entity.uuid,
                                                title = entity.name,
                                                color = entity.colorString,
                                                type = entity.type
                                            )
                                        }
                                        val jsonContent = gson.toJson(syncLists)
                                        val contentStream =
                                            ByteArrayContent.fromString("application/json", jsonContent)

                                        val query =
                                            "name = 'lists.json' and '$listFolderId' in parents and trashed = false"
                                        val existingFile =
                                            driveService?.files()?.list()?.setQ(query)?.execute()?.files?.firstOrNull()

                                        if (existingFile != null) {
                                            driveService?.files()?.update(existingFile.id, null, contentStream)
                                                ?.execute()
                                        }
                                    } catch (e: Exception) {
                                        Log.e("eura-tasks", "Failed to fix lists.json on cloud: ${e.message}")
                                    }
                                }
                            }

                            Log.d("eura-tasks", "Lists reconciled, saved locally, and updated on cloud.")

                            val taskFolderId = getOrCreateSubFolderId(
                                folderName = "tasks",
                                parentFolder = getOrCreateMainFolderId() ?: return@launch
                            ) ?: return@launch

                            val localTasks = taskDao.getAllTasksByIdAsc().first()
                            localTasks.forEach { entity ->
                                try {
                                    val fileName = "task_${entity.uuid}.json"
                                    val syncModel = TodoSyncModel(
                                        uuid = entity.uuid,
                                        title = entity.title,
                                        description = entity.description,
                                        isFavorite = entity.isFavorite,
                                        isCompleted = entity.isCompleted,
                                        date = entity.date,
                                        time = entity.time,
                                        creationTime = entity.creationTime,
                                        taskList = entity.taskList
                                    )
                                    val jsonContent = gson.toJson(syncModel)
                                    val contentStream =
                                        ByteArrayContent.fromString("application/json", jsonContent)

                                    val checkQuery =
                                        "name = '$fileName' and '$taskFolderId' in parents and trashed = false"
                                    val existingFile =
                                        driveService?.files()?.list()?.setQ(checkQuery)?.execute()?.files?.firstOrNull()

                                    if (existingFile != null) {
                                        driveService?.files()?.update(existingFile.id, null, contentStream)
                                            ?.execute()
                                    } else {
                                        val metadata = com.google.api.services.drive.model.File().apply {
                                            name = fileName
                                            parents = listOf(taskFolderId)
                                        }
                                        driveService?.files()?.create(metadata, contentStream)?.execute()
                                    }
                                } catch (e: Exception) {
                                    Log.e("eura-tasks", "Upload failed for task ${entity.uuid}: ${e.message}")
                                }
                            }

                            val cloudTasks = downloadAllTasks()

                            val localDeletedTaskUuids = taskDao.getAllDeletedTasks().map { it.deletedUuid }.toSet()
                            val cloudDeletedTaskUuids = cloudDeletedTasks.map { it.deletedUuid }.toSet()
                            val allDeletedTaskUuids = localDeletedTaskUuids + cloudDeletedTaskUuids

                            cloudDeletedTasks.forEach { remoteDeletedTask ->
                                if (remoteDeletedTask.deletedUuid !in localDeletedTaskUuids) {
                                    taskDao.upsertDeletedTask(remoteDeletedTask)
                                }
                            }

                            val localActiveTaskUuids =
                                taskDao.getAllTasksByIdAsc().first().map { it.uuid }.toSet()

                            cloudTasks.forEach { task ->
                                if (task.uuid in allDeletedTaskUuids) {
                                    deleteTaskFile(task.uuid)
                                    Log.d("eura-tasks", "Cloud task file ${task.uuid} matched a tombstone. Purging from Drive.")
                                } else if (task.uuid !in localActiveTaskUuids) {
                                    taskDbViewModel.insertTask(task)
                                    Log.d("eura-tasks", "Downloaded new cloud task: ${task.title}")
                                }
                            }

                            Log.d("eura-tasks", "--- FULL SYNC COMPLETE ---")

                            withContext(Dispatchers.Main) {
                                onComplete()
                            }
                        }
                    }
                }
            }
        }
    }
}