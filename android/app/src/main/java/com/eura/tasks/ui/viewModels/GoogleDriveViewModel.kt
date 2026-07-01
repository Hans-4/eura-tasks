package com.eura.tasks.ui.viewModels

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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import com.eura.tasks.db.lists.DeletedUserListEntity
import com.eura.tasks.db.lists.ListDbDao
import com.eura.tasks.db.lists.UserListEntity
import com.eura.tasks.db.tags.DeletedTagsEntity
import com.eura.tasks.db.tags.TagDbDao
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.DeletedTasksEntity
import com.eura.tasks.db.tasks.TaskDbDao
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.db.tasks.tags.DeletedTaskTagsEntity
import com.eura.tasks.db.tasks.tags.TaskTagsEntity
import java.time.LocalDateTime
import java.util.Collections
import kotlin.time.Instant

data class TaskSyncModel(
    val uuid: String,
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val isCompleted: Boolean,
    val hasTags: Boolean,
    val taskList: String,
    val dueDateTime: LocalDateTime?,
    val creationTime: Instant,
)

data class ListSyncModel(
    val title: String,
    val type: String,
    val color: String,
    val uuid: String
)

data class DeletedItemSyncModel(
    val uuid: String,
    val deletionDate: Instant
)

data class ListConflict(
    val localList: UserListEntity,
    val remoteList: UserListEntity,
    val onResolved: (UserListEntity) -> Unit
)

data class TagSyncModel(
    val uuid: String,
    val name: String,
)

data class TaskTagsSyncModel(
    val taskUuid: String,
    val tagUUid: String
)

data class DeletedTaskTagsSyncModel(
    val taskUuid: String,
    val tagUUid: String,
    val deletionDate: Instant
)

sealed interface SyncUiState {
    object Idle : SyncUiState
    object Loading : SyncUiState
    data class Success(val message: String) : SyncUiState
    data class Error(val errorMessage: String) : SyncUiState
}

class GoogleDriveViewModel(
    private val taskDao: TaskDbDao,
    private val listDao: ListDbDao,
    private val tagDao: TagDbDao
) : ViewModel() {
    private val _syncMessage = MutableStateFlow("Ready to synchronize.")
    val syncMessage: StateFlow<String> = _syncMessage.asStateFlow()
    private val _syncUiState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
    val syncUiState: StateFlow<SyncUiState> = _syncUiState.asStateFlow()

    private val _listConflict = MutableStateFlow<ListConflict?>(null)
    val listConflict: StateFlow<ListConflict?> = _listConflict.asStateFlow()

    private val gson = GsonBuilder()
        .registerTypeAdapter(
            Instant::class.java,
            com.google.gson.JsonSerializer<Instant> { src, _, _ ->
                com.google.gson.JsonPrimitive(src.toString())
            })
        .registerTypeAdapter(Instant::class.java, com.google.gson.JsonDeserializer { json, _, _ ->
            Instant.parse(json.asString)
        })
        .registerTypeAdapter(
            java.time.LocalDateTime::class.java,
            com.google.gson.JsonSerializer<java.time.LocalDateTime> { src, _, _ ->
                com.google.gson.JsonPrimitive(src.toString())
            })
        .registerTypeAdapter(
            java.time.LocalDateTime::class.java,
            com.google.gson.JsonDeserializer { json, _, _ ->
                java.time.LocalDateTime.parse(json.asString)
            })
        .create()

    private var driveService: Drive? = null

    private val _isDriveServiceReady = MutableStateFlow(false)
    val isDriveServiceReady: StateFlow<Boolean> = _isDriveServiceReady.asStateFlow()

    fun resetSyncStatus() {
        _syncUiState.value = SyncUiState.Idle
        _syncMessage.value = "Ready to synchronize."
    }

    fun initDriveService(context: Context, account: GoogleSignInAccount) {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, Collections.singleton(DriveScopes.DRIVE_FILE)
        ).setSelectedAccount(account.account)

        driveService = Drive.Builder(
            NetHttpTransport(),
            GsonFactory(),
            credential
        ).setApplicationName("eura-tasks").build()

        _isDriveServiceReady.value = true
        Log.d("eura-tasks", "Drive Service Ready for: ${account.email}")
    }

    // Call this when the user logs out
    fun clearDriveService() {
        driveService = null
        _isDriveServiceReady.value = false
        Log.d("eura-tasks", "Drive Service cleared and torn down.")
    }

    fun checkExistingLogin(context: Context) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            initDriveService(context, account)
        }
    }

    private suspend fun getOrCreateMainFolderId(): String? = withContext(Dispatchers.IO) {
        try {
            val mainFolderQuery =
                "name = 'eura-tasks' and mimeType = 'application/vnd.google-apps.folder' and trashed = false"
            val mainResult = driveService?.files()?.list()?.setQ(mainFolderQuery)?.execute()
            var mainFolderId = mainResult?.files?.firstOrNull()?.id

            if (mainFolderId == null) {
                val metadata = com.google.api.services.drive.model.File().apply {
                    name = "eura-tasks"
                    mimeType = "application/vnd.google-apps.folder"
                }
                mainFolderId =
                    driveService?.files()?.create(metadata)?.setFields("id")?.execute()?.id
                Log.d("eura-tasks", "Created Main Folder: $mainFolderId")
            }
            return@withContext mainFolderId
        } catch (e: Exception) {
            Log.e("eura-tasks", "Main folder creation failed: ${e.message}")
            null
        }
    }

    fun deleteAllData(onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val service = driveService
            if (service == null) {
                Log.e("eura-tasks", "Drive Service not initialized")
                withContext(Dispatchers.Main) { onResult(false) }
                return@launch
            }

            try {
                val folderId = getOrCreateMainFolderId() ?: run {
                    withContext(Dispatchers.Main) { onResult(false) }
                    return@launch
                }

                val trashedMetadata = com.google.api.services.drive.model.File().apply {
                    trashed = true
                }

                service.files().update(folderId, trashedMetadata).execute()

                withContext(Dispatchers.Main) { onResult(true) }

            } catch (e: Exception) {
                Log.e("eura-tasks", "Error deleting the main folder: ${e.message}")
                withContext(Dispatchers.Main) { onResult(false) }
            }
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
                    subFolderId =
                        driveService?.files()?.create(metadata)?.setFields("id")?.execute()?.id
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
        transform: suspend (RemoteModel) -> LocalEntity
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

            val query =
                "name = '$fileName' and '$folderId' in parents and mimeType = 'application/json' and trashed = false"
            val existingFile =
                driveService?.files()?.list()?.setQ(query)?.execute()?.files?.firstOrNull()

            if (existingFile != null) {
                val outputStream = java.io.ByteArrayOutputStream()
                driveService?.files()?.get(existingFile.id)?.executeMediaAndDownloadTo(outputStream)

                val jsonString = outputStream.toString()
                val remoteItems =
                    gson.fromJson<List<RemoteModel>>(jsonString, typeToken) ?: emptyList()
                return@withContext remoteItems.map { transform(it) }
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
        transformToLocal: suspend (RemoteModel) -> LocalEntity,
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
                val existingFile =
                    driveService?.files()?.list()?.setQ(query)?.execute()?.files?.firstOrNull()

                if (existingFile != null) {
                    driveService?.files()?.update(existingFile.id, null, contentStream)?.execute()
                    Log.d("eura-tasks", "Updated $fileName on cloud storage.")
                    _syncMessage.value = "Updated $fileName on cloud storage."

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

    fun syncDeletedUserLists(onComplete: (List<DeletedUserListEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val mainFolderId = getOrCreateMainFolderId() ?: return@launch
            val folderId = getOrCreateSubFolderId("deleted_lists", mainFolderId) ?: return@launch

            val localDeletedLists = listDao.getAllDeletedLists().first()
            val typeToken = object : TypeToken<List<DeletedItemSyncModel>>() {}.type

            sync(
                folderName = "deleted_lists",
                folderId = folderId,
                fileName = "deleted_lists.json",
                localData = localDeletedLists,
                transformToRemote = { entity ->
                    DeletedItemSyncModel(
                        uuid = entity.deletedUuid,
                        deletionDate = entity.deletionDate
                    )
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
                        title = entity.title,
                        color = entity.colorString,
                        type = entity.type
                    )
                },
                downloadTypeToken = typeToken,
                transformToLocal = { model ->
                    UserListEntity(
                        uuid = model.uuid,
                        title = model.title,
                        colorString = model.color,
                        type = model.type
                    )
                },
                onComplete = onComplete
            )
        }
    }

    fun syncTags(
        onComplete: (List<TagsEntity>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val mainFolderId = getOrCreateMainFolderId() ?: return@launch
            val folderId = getOrCreateSubFolderId("tags", mainFolderId) ?: return@launch

            val localTags = tagDao.getAllTags().first()
            val typeToken = object : TypeToken<List<TagSyncModel>>() {}.type

            sync(
                folderName = "tags",
                folderId = folderId,
                fileName = "tags.json",
                localData = localTags,
                transformToRemote = { entity ->
                    TagSyncModel(
                        uuid = entity.uuid,
                        name = entity.title
                    )
                },
                downloadTypeToken = typeToken,
                transformToLocal = { model ->
                    TagsEntity(
                        uuid = model.uuid,
                        title = model.name
                    )
                },
                onComplete = onComplete
            )
        }
    }

    fun syncDeletedTags(onComplete: (List<DeletedTagsEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val mainFolderId = getOrCreateMainFolderId() ?: return@launch
            val folderId = getOrCreateSubFolderId("deleted_tags", mainFolderId) ?: return@launch

            val localDeletedTags = tagDao.getAllDeletedTags().first()
            val typeToken = object : TypeToken<List<DeletedItemSyncModel>>() {}.type

            sync(
                folderName = "deleted_tags",
                folderId = folderId,
                fileName = "deleted_tags.json",
                localData = localDeletedTags,
                transformToRemote = { entity ->
                    DeletedItemSyncModel(
                        uuid = entity.deletedUuid,
                        deletionDate = entity.deletionDate
                    )
                },
                downloadTypeToken = typeToken,
                transformToLocal = { model ->
                    DeletedTagsEntity(
                        deletedUuid = model.uuid,
                        deletionDate = model.deletionDate
                    )
                },
                onComplete = onComplete
            )
        }
    }

    fun syncTaskTags(
        onComplete: (List<TaskTagsEntity>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val mainFolderId = getOrCreateMainFolderId() ?: return@launch
            val folderId = getOrCreateSubFolderId("task_tags", mainFolderId) ?: return@launch

            val localTags = tagDao.getAllTaskTags().first()

            val typeToken = object : TypeToken<List<TaskTagsSyncModel>>() {}.type

            sync(
                folderName = "task_tags",
                folderId = folderId,
                fileName = "task_tags.json",
                localData = localTags,
                transformToRemote = { entity ->
                    TaskTagsSyncModel(
                        taskUuid = entity.taskUuid,
                        tagUUid = entity.tagUuid
                    )
                },
                downloadTypeToken = typeToken,
                transformToLocal = { model ->
                    val tagId = tagDao.getTagIdByUuid(model.tagUUid)
                    val taskId = taskDao.getTaskIdByUuid(model.taskUuid)

                    TaskTagsEntity(
                        taskId = taskId,
                        taskUuid = model.taskUuid,
                        tagId = tagId,
                        tagUuid = model.tagUUid
                    )
                },
                onComplete = onComplete
            )
        }
    }

    fun syncDeletedTaskTags(
        onComplete: (List<DeletedTaskTagsEntity>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val mainFolderId = getOrCreateMainFolderId() ?: return@launch
            val folderId =
                getOrCreateSubFolderId("deleted_task_tags", mainFolderId) ?: return@launch

            val localTags = tagDao.getAllDeletedTaskTags().first()

            val typeToken = object : TypeToken<List<DeletedTaskTagsSyncModel>>() {}.type

            sync(
                folderName = "deleted_task_tags",
                folderId = folderId,
                fileName = "deleted_task_tags.json",
                localData = localTags,
                transformToRemote = { entity ->
                    DeletedTaskTagsSyncModel(
                        taskUuid = entity.deletedTaskUuid,
                        tagUUid = entity.deletedTagUuid,
                        deletionDate = entity.deletionDate
                    )
                },
                downloadTypeToken = typeToken,
                transformToLocal = { model ->
                    DeletedTaskTagsEntity(
                        deletedTaskUuid = model.taskUuid,
                        deletedTagUuid = model.tagUUid,
                        deletionDate = model.deletionDate,
                    )
                },
                onComplete = onComplete
            )
        }
    }

    fun syncDeletedTasks(onComplete: (List<DeletedTasksEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val mainFolderId = getOrCreateMainFolderId() ?: return@launch
            val folderId = getOrCreateSubFolderId("deleted_tasks", mainFolderId) ?: return@launch

            val localDeletedTasks = taskDao.getAllDeletedTasks()
            val typeToken = object : TypeToken<List<DeletedItemSyncModel>>() {}.type

            sync(
                folderName = "deleted_tasks",
                folderId = folderId,
                fileName = "deleted_tasks.json",
                localData = localDeletedTasks,
                transformToRemote = { entity ->
                    DeletedItemSyncModel(
                        uuid = entity.deletedUuid,
                        deletionDate = entity.deletionDate
                    )
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

    suspend fun downloadAllTasks(): List<TaskEntity> = withContext(Dispatchers.IO) {
        val downloadedTasks = mutableListOf<TaskEntity>()
        if (driveService == null) {
            Log.e("eura-tasks", "Drive Service not initialized")
            return@withContext emptyList()
        }

        try {
            val folderId = getOrCreateSubFolderId(
                folderName = "tasks",
                parentFolder = getOrCreateMainFolderId() ?: return@withContext emptyList()
            ) ?: return@withContext emptyList()

            val query =
                "'$folderId' in parents and mimeType = 'application/json' and trashed = false"
            val fileList =
                driveService?.files()?.list()?.setQ(query)?.execute()?.files ?: emptyList()

            fileList.forEach { file ->
                try {
                    val outputStream = java.io.ByteArrayOutputStream()
                    driveService?.files()?.get(file.id)?.executeMediaAndDownloadTo(outputStream)

                    val jsonString = outputStream.toString()
                    val syncModel = gson.fromJson(jsonString, TaskSyncModel::class.java)

                    downloadedTasks.add(
                        TaskEntity(
                            uuid = syncModel.uuid,
                            title = syncModel.title,
                            description = syncModel.description,
                            isFavorite = syncModel.isFavorite,
                            isCompleted = syncModel.isCompleted,
                            hasTags = syncModel.hasTags,
                            dueDateTime = syncModel.dueDateTime,
                            creationTime = syncModel.creationTime,
                            taskList = syncModel.taskList,
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

                val existingFile =
                    driveService?.files()?.list()?.setQ(query)?.execute()?.files?.firstOrNull()

                if (existingFile != null && existingFile.id != null) {
                    val trashedMetadata = com.google.api.services.drive.model.File().apply {
                        trashed = true
                    }
                    driveService?.files()?.update(existingFile.id, trashedMetadata)?.execute()
                    Log.d("eura-tasks", "Moved to trash: $fileName")
                    _syncMessage.value = "Moved to trash: $fileName"
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
            _syncUiState.value = SyncUiState.Loading
            Log.d("eura-tasks", "--- STARTING FULL SYNC ---")
            _syncMessage.value = "Starting sync..."


            syncDeletedUserLists { cloudDeletedLists ->
                syncUserLists { cloudActiveLists ->
                    syncDeletedTags { cloudDeletedTags ->
                        syncTags { cloudActiveTags ->
                            syncDeletedTasks { cloudDeletedTasks ->
                                syncDeletedTaskTags { remoteDeletedTaskTags ->
                                    syncTaskTags { remoteActiveTaskTags ->
                                        viewModelScope.launch(Dispatchers.IO) {
                                            val localDeletedListUuids =
                                                listDao.getAllDeletedLists().first()
                                                    .map { it.deletedUuid }
                                                    .toSet()
                                            val cloudDeletedListUuids =
                                                cloudDeletedLists.map { it.deletedUuid }.toSet()
                                            val allDeletedListUuids =
                                                localDeletedListUuids + cloudDeletedListUuids

                                            cloudDeletedLists.forEach { remoteDeletedList ->
                                                if (remoteDeletedList.deletedUuid !in localDeletedListUuids) {
                                                    listDao.upsertDeletedList(remoteDeletedList)
                                                }
                                            }

                                            val localActiveLists = listDao.getAllLists().first()
                                            val localActiveListUuids =
                                                localActiveLists.map { it.uuid }.toSet()

                                            val cleanActiveLists = cloudActiveLists.filter { list ->
                                                list.uuid !in allDeletedListUuids
                                            }

                                            var needsReupload = false

                                            for (cloudList in cleanActiveLists) {
                                                if (cloudList.uuid !in localActiveListUuids) {
                                                    val conflict =
                                                        localActiveLists.find { it.title == cloudList.title }
                                                    if (conflict != null) {
                                                        val deferred =
                                                            CompletableDeferred<UserListEntity>()
                                                        _listConflict.value = ListConflict(
                                                            localList = conflict,
                                                            remoteList = cloudList,
                                                            onResolved = { resolved ->
                                                                _listConflict.value = null
                                                                deferred.complete(resolved)
                                                            }
                                                        )
                                                        val winner = deferred.await()
                                                        if (winner === cloudList) {
                                                            listDao.upsertList(
                                                                conflict.copy(
                                                                    uuid = cloudList.uuid,
                                                                    colorString = cloudList.colorString,
                                                                    type = cloudList.type
                                                                )
                                                            )
                                                            Log.d(
                                                                "eura-tasks",
                                                                "Conflict resolved: Kept cloud list ${cloudList.title}"
                                                            )
                                                            _syncMessage.value =
                                                                "Conflict resolved: Kept cloud list ${cloudList.title}"
                                                            needsReupload = true
                                                        } else {
                                                            Log.d(
                                                                "eura-tasks",
                                                                "Conflict resolved: Kept local list ${conflict.title}"
                                                            )
                                                            _syncMessage.value =
                                                                "Conflict resolved: Kept local list ${conflict.title}"
                                                        }
                                                    } else {
                                                        listDbViewModel.insertList(
                                                            name = cloudList.title,
                                                            color = cloudList.colorString,
                                                            type = cloudList.type,
                                                            uuid = cloudList.uuid
                                                        )
                                                        Log.d(
                                                            "eura-tasks",
                                                            "Downloaded new cloud list: ${cloudList.title}"
                                                        )
                                                        _syncMessage.value =
                                                            "Downloaded new cloud list: ${cloudList.title}"
                                                    }
                                                }
                                            }

                                            if (cleanActiveLists.size < cloudActiveLists.size || needsReupload) {
                                                if (needsReupload) {
                                                    Log.d(
                                                        "eura-tasks",
                                                        "Conflicts resolved. Re-uploading lists.json to Drive."
                                                    )
                                                    _syncMessage.value =
                                                        "Conflicts resolved. Re-uploading lists.json to Drive."

                                                } else {
                                                    Log.d(
                                                        "eura-tasks",
                                                        "Detected zombie lists on cloud. Re-uploading cleaned lists.json to Drive."
                                                    )
                                                    _syncMessage.value =
                                                        "Detected zombie lists on cloud. Re-uploading cleaned lists.json to Drive."
                                                }

                                                val listFolderId = getOrCreateSubFolderId(
                                                    "lists",
                                                    getOrCreateMainFolderId()
                                                )
                                                if (listFolderId != null) {
                                                    try {
                                                        val finalLocalLists =
                                                            listDao.getAllLists().first()
                                                        val syncLists =
                                                            finalLocalLists.map { entity ->
                                                                ListSyncModel(
                                                                    uuid = entity.uuid,
                                                                    title = entity.title,
                                                                    color = entity.colorString,
                                                                    type = entity.type
                                                                )
                                                            }
                                                        val jsonContent = gson.toJson(syncLists)
                                                        val contentStream =
                                                            ByteArrayContent.fromString(
                                                                "application/json",
                                                                jsonContent
                                                            )

                                                        val query =
                                                            "name = 'lists.json' and '$listFolderId' in parents and trashed = false"
                                                        val existingFile =
                                                            driveService?.files()?.list()
                                                                ?.setQ(query)
                                                                ?.execute()?.files?.firstOrNull()

                                                        if (existingFile != null) {
                                                            driveService?.files()?.update(
                                                                existingFile.id,
                                                                null,
                                                                contentStream
                                                            )
                                                                ?.execute()
                                                        }
                                                    } catch (e: Exception) {
                                                        Log.e(
                                                            "eura-tasks",
                                                            "Failed to fix lists.json on cloud: ${e.message}"
                                                        )
                                                        _syncUiState.value =
                                                            SyncUiState.Error("Failed to fix lists.json on cloud: ${e.message}")
                                                    }
                                                }
                                            }

                                            Log.d(
                                                "eura-tasks",
                                                "Lists reconciled, saved locally, and updated on cloud."
                                            )
                                            _syncUiState.value =
                                                SyncUiState.Success("Lists reconciled, saved locally, and updated on cloud.")


                                            // Reconcile Tags
                                            val localDeletedTagUuids =
                                                tagDao.getAllDeletedTags().first()
                                                    .map { it.deletedUuid }
                                                    .toSet()
                                            val cloudDeletedTagUuids =
                                                cloudDeletedTags.map { it.deletedUuid }.toSet()
                                            val allDeletedTagUuids =
                                                localDeletedTagUuids + cloudDeletedTagUuids

                                            cloudDeletedTags.forEach { remoteDeletedTag ->
                                                if (remoteDeletedTag.deletedUuid !in localDeletedTagUuids) {
                                                    tagDao.upsertDeletedTag(remoteDeletedTag)
                                                }
                                            }

                                            val localActiveTags = tagDao.getAllTags().first()
                                            val localActiveTagUuids =
                                                localActiveTags.map { it.uuid }.toSet()

                                            val cleanActiveTags = cloudActiveTags.filter { tag ->
                                                tag.uuid !in allDeletedTagUuids
                                            }

                                            cleanActiveTags.forEach { cloudTag ->
                                                if (cloudTag.uuid !in localActiveTagUuids) {
                                                    tagDao.upsertTag(cloudTag)
                                                    Log.d(
                                                        "eura-tasks",
                                                        "Downloaded new cloud tag: ${cloudTag.title}"
                                                    )
                                                    _syncMessage.value =
                                                        "Downloaded new cloud tag: ${cloudTag.title}"
                                                }
                                            }

                                            if (cleanActiveTags.size < cloudActiveTags.size) {
                                                val tagFolderId = getOrCreateSubFolderId(
                                                    "tags",
                                                    getOrCreateMainFolderId()
                                                )
                                                if (tagFolderId != null) {
                                                    try {
                                                        val finalLocalTags =
                                                            tagDao.getAllTags().first()
                                                        val syncTags =
                                                            finalLocalTags.map { entity ->
                                                                TagSyncModel(
                                                                    uuid = entity.uuid,
                                                                    name = entity.title
                                                                )
                                                            }
                                                        val jsonContent = gson.toJson(syncTags)
                                                        val contentStream =
                                                            ByteArrayContent.fromString(
                                                                "application/json",
                                                                jsonContent
                                                            )
                                                        val query =
                                                            "name = 'tags.json' and '$tagFolderId' in parents and trashed = false"
                                                        val existingFile =
                                                            driveService?.files()?.list()
                                                                ?.setQ(query)
                                                                ?.execute()?.files?.firstOrNull()
                                                        if (existingFile != null) {
                                                            driveService?.files()?.update(
                                                                existingFile.id,
                                                                null,
                                                                contentStream
                                                            )?.execute()
                                                        }
                                                    } catch (e: Exception) {
                                                        Log.e(
                                                            "eura-tasks",
                                                            "Failed to fix tags.json on cloud: ${e.message}"
                                                        )
                                                    }
                                                }
                                            }

                                            Log.d(
                                                "eura-tasks",
                                                "Tags reconciled, saved locally, and updated on cloud."
                                            )
                                            _syncUiState.value =
                                                SyncUiState.Success("Tags reconciled, saved locally, and updated on cloud.")


                                            val taskFolderId = getOrCreateSubFolderId(
                                                folderName = "tasks",
                                                parentFolder = getOrCreateMainFolderId()
                                                    ?: return@launch
                                            ) ?: return@launch

                                            val localTasks = taskDao.getAllTasksByIdAsc().first()
                                            localTasks.forEach { entity ->
                                                try {
                                                    val fileName = "task_${entity.uuid}.json"
                                                    val syncModel = TaskSyncModel(
                                                        uuid = entity.uuid,
                                                        title = entity.title,
                                                        description = entity.description,
                                                        isFavorite = entity.isFavorite,
                                                        isCompleted = entity.isCompleted,
                                                        hasTags = entity.hasTags,
                                                        dueDateTime = entity.dueDateTime,
                                                        creationTime = entity.creationTime,
                                                        taskList = entity.taskList
                                                    )
                                                    val jsonContent = gson.toJson(syncModel)
                                                    val contentStream =
                                                        ByteArrayContent.fromString(
                                                            "application/json",
                                                            jsonContent
                                                        )

                                                    val checkQuery =
                                                        "name = '$fileName' and '$taskFolderId' in parents and trashed = false"
                                                    val existingFile =
                                                        driveService?.files()?.list()
                                                            ?.setQ(checkQuery)
                                                            ?.execute()?.files?.firstOrNull()

                                                    if (existingFile != null) {
                                                        driveService?.files()
                                                            ?.update(
                                                                existingFile.id,
                                                                null,
                                                                contentStream
                                                            )
                                                            ?.execute()
                                                    } else {
                                                        val metadata =
                                                            com.google.api.services.drive.model.File()
                                                                .apply {
                                                                    name = fileName
                                                                    parents = listOf(taskFolderId)
                                                                }
                                                        driveService?.files()
                                                            ?.create(metadata, contentStream)
                                                            ?.execute()
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e(
                                                        "eura-tasks",
                                                        "Upload failed for task ${entity.uuid}: ${e.message}"
                                                    )
                                                    _syncUiState.value =
                                                        SyncUiState.Error("Upload failed for task ${entity.uuid}: ${e.message}")
                                                }
                                            }

                                            val cloudTasks = downloadAllTasks()

                                            val localDeletedTaskUuids =
                                                taskDao.getAllDeletedTasks().map { it.deletedUuid }
                                                    .toSet()
                                            val cloudDeletedTaskUuids =
                                                cloudDeletedTasks.map { it.deletedUuid }.toSet()
                                            val allDeletedTaskUuids =
                                                localDeletedTaskUuids + cloudDeletedTaskUuids

                                            cloudDeletedTasks.forEach { remoteDeletedTask ->
                                                if (remoteDeletedTask.deletedUuid !in localDeletedTaskUuids) {
                                                    taskDao.upsertDeletedTask(remoteDeletedTask)
                                                }
                                            }

                                            val localActiveTaskUuids =
                                                taskDao.getAllTasksByIdAsc().first().map { it.uuid }
                                                    .toSet()

                                            // Get names of lists that are currently available locally
                                            val localActiveListNames =
                                                listDao.getAllLists().first().map { it.title }
                                                    .toMutableSet()

                                            cloudTasks.forEach { task ->
                                                if (task.uuid in allDeletedTaskUuids) {
                                                    deleteTaskFile(task.uuid)
                                                    Log.d(
                                                        "eura-tasks",
                                                        "Cloud task file ${task.uuid} matched a tombstone. Purging from Drive."
                                                    )
                                                    _syncMessage.value =
                                                        "Cloud task file ${task.uuid} matched a tombstone. Purging from Drive."

                                                } else if (task.uuid !in localActiveTaskUuids) {

                                                    // CHECK: Does the target list exist locally?
                                                    if (task.taskList !in localActiveListNames) {
                                                        Log.w(
                                                            "eura-tasks",
                                                            "List '${task.taskList}' missing for downloaded task. Auto-creating list."
                                                        )
                                                        _syncUiState.value =
                                                            SyncUiState.Success("List '${task.taskList}' missing for downloaded task. Auto-creating list.")

                                                        // Synchronously insert the missing list into Room first
                                                        listDbViewModel.insertListSynchronously(
                                                            name = task.taskList,
                                                            type = "OTHER",
                                                            color = "PURPLE"
                                                        )
                                                        // Track it locally so we don't accidentally re-create it on subsequent tasks
                                                        localActiveListNames.add(task.taskList)
                                                    }

                                                    taskDbViewModel.insertTask(task)
                                                    Log.d(
                                                        "eura-tasks",
                                                        "Downloaded new cloud task: ${task.title}"
                                                    )
                                                    _syncMessage.value =
                                                        "Downloaded new cloud task: ${task.title}"

                                                }
                                            }

                                            // Reconcile Task Tags (Associations)
                                            val localDeletedTaskTags =
                                                tagDao.getAllDeletedTaskTags().first()
                                            val allDeletedTaskTags =
                                                localDeletedTaskTags + remoteDeletedTaskTags

                                            remoteDeletedTaskTags.forEach { remoteDeletedTaskTag ->
                                                if (localDeletedTaskTags.none { it.deletedTaskUuid == remoteDeletedTaskTag.deletedTaskUuid && it.deletedTagUuid == remoteDeletedTaskTag.deletedTagUuid }) {
                                                    tagDao.upsertDeletedTaskTag(remoteDeletedTaskTag)
                                                }
                                            }

                                            val localActiveTaskTags =
                                                tagDao.getAllTaskTags().first()

                                            val cleanRemoteActiveTaskTags =
                                                remoteActiveTaskTags.filter { taskTag ->
                                                    allDeletedTaskTags.none { deleted ->
                                                        deleted.deletedTaskUuid == taskTag.taskUuid && deleted.deletedTagUuid == taskTag.tagUuid
                                                    }
                                                }

                                            cleanRemoteActiveTaskTags.forEach { cloudTaskTag ->
                                                if (localActiveTaskTags.none { it.taskUuid == cloudTaskTag.taskUuid && it.tagUuid == cloudTaskTag.tagUuid }) {
                                                    val taskId =
                                                        taskDao.getTaskIdByUuid(cloudTaskTag.taskUuid)
                                                    val tagId =
                                                        tagDao.getTagIdByUuid(cloudTaskTag.tagUuid)

                                                    if (taskId != 0 && tagId != 0) {
                                                        tagDao.insertTaskTag(
                                                            cloudTaskTag.copy(
                                                                taskId = taskId,
                                                                tagId = tagId
                                                            )
                                                        )
                                                        Log.d(
                                                            "eura-tasks",
                                                            "Restored task-tag association: ${cloudTaskTag.taskUuid} <-> ${cloudTaskTag.tagUuid}"
                                                        )
                                                        _syncMessage.value =
                                                            "Restored task-tag association: ${cloudTaskTag.taskUuid} <-> ${cloudTaskTag.tagUuid}"
                                                    }
                                                }
                                            }

                                            // Re-upload cleaned task tags to cloud if any were purged
                                            if (cleanRemoteActiveTaskTags.size < remoteActiveTaskTags.size) {
                                                val taskTagFolderId = getOrCreateSubFolderId(
                                                    "task_tags",
                                                    getOrCreateMainFolderId()
                                                )
                                                if (taskTagFolderId != null) {
                                                    try {
                                                        val finalLocalTaskTags =
                                                            tagDao.getAllTaskTags().first()
                                                        val syncTaskTags =
                                                            finalLocalTaskTags.map { entity ->
                                                                TaskTagsSyncModel(
                                                                    taskUuid = entity.taskUuid,
                                                                    tagUUid = entity.tagUuid
                                                                )
                                                            }
                                                        val jsonContent = gson.toJson(syncTaskTags)
                                                        val contentStream =
                                                            ByteArrayContent.fromString(
                                                                "application/json",
                                                                jsonContent
                                                            )
                                                        val query =
                                                            "name = 'task_tags.json' and '$taskTagFolderId' in parents and trashed = false"
                                                        val existingFile =
                                                            driveService?.files()?.list()
                                                                ?.setQ(query)
                                                                ?.execute()?.files?.firstOrNull()
                                                        if (existingFile != null) {
                                                            driveService?.files()?.update(
                                                                existingFile.id,
                                                                null,
                                                                contentStream
                                                            )?.execute()
                                                        }
                                                    } catch (e: Exception) {
                                                        Log.e(
                                                            "eura-tasks",
                                                            "Failed to fix task_tags.json on cloud: ${e.message}"
                                                        )
                                                    }
                                                }
                                            }

                                            Log.d("eura-tasks", "--- FULL SYNC COMPLETE ---")

                                            withContext(Dispatchers.Main) {
                                                _syncUiState.value =
                                                    SyncUiState.Success("Sync completed successfully")
                                                onComplete()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}