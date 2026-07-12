package com.eura.tasks.ui.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eura.tasks.db.deletedItems.DeletedItemsDao
import com.eura.tasks.db.deletedItems.DeletedItemsEntity
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
import com.eura.tasks.db.lists.ListDbDao
import com.eura.tasks.db.lists.UserListEntity
import com.eura.tasks.db.tags.TagDbDao
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.TaskDbDao
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.db.tasks.tags.DeletedTaskTagsEntity
import com.eura.tasks.db.tasks.tags.TaskTagsEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.Collections

data class TaskSyncModel(
    val uuid: String,
    val title: String,
    val description: String?,
    val isFavorite: Boolean,
    val isCompleted: Boolean,
    val hasTags: Boolean,
    val parentListId: String,
    val dueDateTime: Instant?,
    val creationTime: Instant,
    val updateTime: Instant
)

data class ListSyncModel(
    val id: String,
    val title: String,
    val type: String,
    val color: String,
    val creationTime: Instant,
    val updateTime: Instant
)

data class DeletedItemsSyncModel(
    val id: String,
    val deletionTime: Instant,
    val type: Int
)

data class ListConflict(
    val localList: UserListEntity,
    val remoteList: UserListEntity,
    val onResolved: (UserListEntity) -> Unit
)

data class TagSyncModel(
    val uuid: String,
    val name: String,
    val creationTime: Instant,
    val updateTime: Instant
)

data class TaskTagsSyncModel(
    val taskUuid: String,
    val tagUUid: String,
    val isActive: Boolean,
    val updateTime: Instant
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
    private val tagDao: TagDbDao,

    private val deletedItemsDao: DeletedItemsDao
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

    private suspend fun <RemoteModel, LocalEntity> sync(
        folderName: String,
        folderId: String,
        fileName: String,
        localData: List<LocalEntity>,
        transformToRemote: (LocalEntity) -> RemoteModel,
        downloadTypeToken: java.lang.reflect.Type,
        transformToLocal: suspend (RemoteModel) -> LocalEntity
    ): List<LocalEntity> = withContext(Dispatchers.IO) {
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

        return@withContext remoteData
    }

    suspend fun syncUserLists(): List<UserListEntity> = withContext(Dispatchers.IO) {
        val mainFolderId = getOrCreateMainFolderId() ?: return@withContext emptyList()
        val folderId = getOrCreateSubFolderId("lists", mainFolderId) ?: return@withContext emptyList()

        val localLists = listDao.getAllLists().first()
        val typeToken = object : TypeToken<List<ListSyncModel>>() {}.type

        return@withContext sync(
            folderName = "lists",
            folderId = folderId,
            fileName = "lists.json",
            localData = localLists,
            transformToRemote = { entity ->
                ListSyncModel(
                    id = entity.listId,
                    title = entity.title,
                    color = entity.colorString,
                    type = entity.type,
                    creationTime = entity.creationTime,
                    updateTime = entity.updateTime
                )
            },
            downloadTypeToken = typeToken,
            transformToLocal = { model ->
                UserListEntity(
                    listId = model.id,
                    title = model.title,
                    colorString = model.color,
                    type = model.type,
                    creationTime = model.creationTime,
                    updateTime = model.updateTime
                )
            }
        )
    }

    suspend fun syncTags(): List<TagsEntity> = withContext(Dispatchers.IO) {
        val mainFolderId = getOrCreateMainFolderId() ?: return@withContext emptyList()
        val folderId = getOrCreateSubFolderId("tags", mainFolderId) ?: return@withContext emptyList()

        val localTags = tagDao.getAllTags().first()
        val typeToken = object : TypeToken<List<TagSyncModel>>() {}.type

        return@withContext sync(
            folderName = "tags",
            folderId = folderId,
            fileName = "tags.json",
            localData = localTags,
            transformToRemote = { entity ->
                TagSyncModel(
                    uuid = entity.tagUuid,
                    name = entity.title,
                    creationTime = entity.creationTime,
                    updateTime = entity.updateTime
                )
            },
            downloadTypeToken = typeToken,
            transformToLocal = { model ->
                TagsEntity(
                    tagUuid = model.uuid,
                    title = model.name,
                    creationTime = model.creationTime,
                    updateTime = model.updateTime
                )
            }
        )
    }

    suspend fun downloadTaskTags(): List<TaskTagsEntity> = withContext(Dispatchers.IO) {
        val downloadedTaskTags = mutableListOf<TaskTagsEntity>()
        if (driveService == null) {
            Log.e("eura-tasks", "Drive Service not initialized")
            return@withContext emptyList()
        }

        try {
            val folderId = getOrCreateSubFolderId(
                folderName = "task_tags",
                parentFolder = getOrCreateMainFolderId() ?: return@withContext emptyList()
            ) ?: return@withContext emptyList()

            val query = "'$folderId' in parents and mimeType = 'application/json' and trashed = false"
            val fileList = driveService?.files()?.list()?.setQ(query)?.execute()?.files ?: emptyList()

            fileList.forEach { file ->
                try {
                    val outputStream = java.io.ByteArrayOutputStream()
                    driveService?.files()?.get(file.id)?.executeMediaAndDownloadTo(outputStream)

                    val jsonString = outputStream.toString()
                    val syncModel = gson.fromJson(jsonString, TaskTagsSyncModel::class.java)

                    downloadedTaskTags.add(
                        TaskTagsEntity(
                            taskUuid = syncModel.taskUuid,
                            tagUuid = syncModel.tagUUid,
                            isActive = syncModel.isActive,
                            updateTime = syncModel.updateTime
                        )
                    )
                } catch (e: Exception) {
                    Log.e("eura-tasks", "Failed to download/parse file ${file.name}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("eura-tasks", "Download list failed: ${e.message}")
        }
        return@withContext downloadedTaskTags
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

            val query = "'$folderId' in parents and mimeType = 'application/json' and trashed = false"
            val fileList = driveService?.files()?.list()?.setQ(query)?.execute()?.files ?: emptyList()

            fileList.forEach { file ->
                try {
                    val outputStream = java.io.ByteArrayOutputStream()
                    driveService?.files()?.get(file.id)?.executeMediaAndDownloadTo(outputStream)

                    val jsonString = outputStream.toString()
                    val syncModel = gson.fromJson(jsonString, TaskSyncModel::class.java)

                    downloadedTasks.add(
                        TaskEntity(
                            taskUuid = syncModel.uuid,
                            title = syncModel.title,
                            description = syncModel.description,
                            isFavorite = syncModel.isFavorite,
                            isCompleted = syncModel.isCompleted,
                            hasTags = syncModel.hasTags,
                            notificationTime = syncModel.dueDateTime,
                            creationTime = syncModel.creationTime,
                            updateTime = syncModel.updateTime,
                            parentListId = syncModel.parentListId,
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

    fun deleteTaskFile(
        type: String,
        uuid: String,
        onResult: (Boolean) -> Unit = {},
        folderName: String,
        ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (driveService == null) {
                Log.e("eura-tasks", "Drive Service not initialized")
                withContext(Dispatchers.Main) { onResult(false) }
                return@launch
            }
            try {
                val folderId = getOrCreateSubFolderId(
                    folderName = folderName,
                    parentFolder = getOrCreateMainFolderId() ?: run {
                        withContext(Dispatchers.Main) { onResult(false) }
                        return@launch
                    }
                ) ?: run {
                    withContext(Dispatchers.Main) { onResult(false) }
                    return@launch
                }

                val fileName = "${type}_$uuid.json"
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


    /**
     * This function gets all db entry and create `.sync` folder
     */
    suspend fun syncDeletedItems(): List<DeletedItemsEntity> = withContext(Dispatchers.IO) {
        val mainFolderId = getOrCreateMainFolderId() ?: return@withContext emptyList()
        val folderId = getOrCreateSubFolderId(".sync", mainFolderId) ?: return@withContext emptyList()

        val localDeletedItems = deletedItemsDao.getAllDeletedItems().first()
        val typeToken = object : TypeToken<List<DeletedItemsSyncModel>>() {}.type

        return@withContext sync(
            folderName = ".sync",
            folderId = folderId,
            fileName = "deleted_items.json",
            localData = localDeletedItems,
            transformToRemote = { entity ->
                DeletedItemsSyncModel(
                    id = entity.deletedUuid,
                    deletionTime = entity.deletionTime,
                    type = entity.type
                )
            },
            downloadTypeToken = typeToken,
            transformToLocal = { model ->
                DeletedItemsEntity(
                    deletedUuid = model.id,
                    deletionTime = model.deletionTime,
                    type = model.type
                )
            }
        )
    }



    /**
     * This functions syncs all cloud entrys into the db
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
            _syncUiState.value = SyncUiState.Loading
            Log.d("eura-tasks", "--- STARTING FULL SYNC ---")
            _syncMessage.value = "Starting sync..."

            try {
                val cloudDeletedItems = syncDeletedItems()
                val cloudActiveLists = syncUserLists()
                val cloudActiveTags = syncTags()


                // 2. Reconciliation Logic
                val localDeletedItemUuids = deletedItemsDao.getAllDeletedItems()
                    .first()
                    .map { it.deletedUuid }
                    .toSet()

                val cloudDeletedItemUuids = cloudDeletedItems
                    .map { it.deletedUuid }
                    .toSet()

                val allDeletedItemUuids = localDeletedItemUuids + cloudDeletedItemUuids

                // Upserts all cloud deleted items not in DB
                cloudDeletedItems.forEach { remoteDeletedItem ->
                    if (remoteDeletedItem.deletedUuid !in localDeletedItemUuids) {
                        deletedItemsDao.upsertDeletedItem(remoteDeletedItem)
                    }
                }

                val localActiveLists = listDao.getAllLists().first()
                val localActiveListUuids = localActiveLists.map { it.listId }.toMutableSet()
                Log.d("eura-tasks", "Local Active List Uuids: $localActiveListUuids")
                Log.d("eura-tasks", "Cloud Active Lists: $cloudActiveLists")

                val cleanActiveCloudLists = cloudActiveLists.filter { list ->
                    list.listId !in allDeletedItemUuids
                }
                Log.d("eura-tasks", "Clean Active Cloud Lists: $cleanActiveCloudLists")

                var needsReupload = false

                for (cloudList in cleanActiveCloudLists) {
                    if (cloudList.listId !in localActiveListUuids) {
                        val conflict = localActiveLists.find { it.title == cloudList.title }
                        if (conflict != null) {
                            val deferred = CompletableDeferred<UserListEntity>()
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
                                        listId = cloudList.listId,
                                        colorString = cloudList.colorString,
                                        type = cloudList.type
                                    )
                                )
                                Log.d("eura-tasks", "Conflict resolved: Kept cloud list ${cloudList.title}")
                                _syncMessage.value = "Conflict resolved: Kept cloud list ${cloudList.title}"
                                needsReupload = true
                            } else {
                                Log.d("eura-tasks", "Conflict resolved: Kept local list ${conflict.title}")
                                _syncMessage.value = "Conflict resolved: Kept local list ${conflict.title}"
                            }
                        } else {
                            listDbViewModel.insertList(
                                name = cloudList.title,
                                color = cloudList.colorString,
                                type = cloudList.type,
                                creationTime = cloudList.creationTime,
                                updateTime = cloudList.updateTime,
                                uuid = cloudList.listId
                            )
                            Log.d("eura-tasks", "Downloaded new cloud list: ${cloudList.title}")
                            _syncMessage.value = "Downloaded new cloud list: ${cloudList.title}"
                            localActiveListUuids.add(cloudList.listId)
                        }
                    }
                }

                if (cleanActiveCloudLists.size < cloudActiveLists.size || needsReupload) {
                    val listFolderId = getOrCreateSubFolderId("lists", getOrCreateMainFolderId())
                    if (listFolderId != null) {
                        try {
                            val finalLocalLists = listDao.getAllLists().first()
                            val syncLists = finalLocalLists.map { entity ->
                                ListSyncModel(
                                    id = entity.listId,
                                    title = entity.title,
                                    color = entity.colorString,
                                    type = entity.type,
                                    creationTime = entity.creationTime,
                                    updateTime = entity.updateTime
                                )
                            }
                            val jsonContent = gson.toJson(syncLists)
                            val contentStream = ByteArrayContent.fromString("application/json", jsonContent)
                            val query = "name = 'lists.json' and '$listFolderId' in parents and trashed = false"
                            val existingFile = driveService?.files()?.list()?.setQ(query)?.execute()?.files?.firstOrNull()
                            if (existingFile != null) {
                                driveService?.files()?.update(existingFile.id, null, contentStream)?.execute()
                            }
                        } catch (e: Exception) {
                            Log.e("eura-tasks", "Failed to fix lists.json on cloud: ${e.message}")
                        }
                    }
                }

                Log.d("eura-tasks", "Lists reconciled, saved locally, and updated on cloud.")
                _syncUiState.value = SyncUiState.Success("Lists reconciled, saved locally, and updated on cloud.")

                val localActiveTags = tagDao.getAllTags().first()
                val localActiveTagUuids = localActiveTags.map { it.tagUuid }.toSet()
                val cleanActiveCloudTags = cloudActiveTags.filter { tag ->
                    tag.tagUuid !in allDeletedItemUuids
                }

                cleanActiveCloudTags.forEach { cloudTag ->
                    if (cloudTag.tagUuid !in localActiveTagUuids) {
                        tagDao.upsertTag(cloudTag)
                        Log.d("eura-tasks", "Downloaded new cloud tag: ${cloudTag.title}")
                        _syncMessage.value = "Downloaded new cloud tag: ${cloudTag.title}"
                    }
                }

                if (cleanActiveCloudTags.size < cloudActiveTags.size) {
                    val tagFolderId = getOrCreateSubFolderId("tags", getOrCreateMainFolderId())
                    if (tagFolderId != null) {
                        try {
                            val finalLocalTags = tagDao.getAllTags().first()
                            val syncTags = finalLocalTags.map { entity ->
                                TagSyncModel(
                                    uuid = entity.tagUuid,
                                    name = entity.title,
                                    creationTime = entity.creationTime,
                                    updateTime = entity.updateTime
                                )
                            }
                            val jsonContent = gson.toJson(syncTags)
                            val contentStream = ByteArrayContent.fromString("application/json", jsonContent)
                            val query = "name = 'tags.json' and '$tagFolderId' in parents and trashed = false"
                            val existingFile = driveService?.files()?.list()?.setQ(query)?.execute()?.files?.firstOrNull()
                            if (existingFile != null) {
                                driveService?.files()?.update(existingFile.id, null, contentStream)?.execute()
                            }
                        } catch (e: Exception) {
                            Log.e("eura-tasks", "Failed to fix tags.json on cloud: ${e.message}")
                        }
                    }
                }

                Log.d("eura-tasks", "Tags reconciled, saved locally, and updated on cloud.")
                _syncUiState.value = SyncUiState.Success("Tags reconciled, saved locally, and updated on cloud.")

                val taskFolderId = getOrCreateSubFolderId(folderName = "tasks", parentFolder = getOrCreateMainFolderId() ?: return@launch) ?: return@launch
                val localTasks = taskDao.getAllTasksByUuidAsc().first()
                localTasks.forEach { entity ->
                    try {
                        val fileName = "task_${entity.taskUuid}.json"
                        val syncModel = TaskSyncModel(
                            uuid = entity.taskUuid,
                            title = entity.title,
                            description = entity.description,
                            isFavorite = entity.isFavorite,
                            isCompleted = entity.isCompleted,
                            hasTags = entity.hasTags,
                            dueDateTime = entity.notificationTime,
                            creationTime = entity.creationTime,
                            updateTime = entity.updateTime,
                            parentListId = entity.parentListId
                        )
                        val jsonContent = gson.toJson(syncModel)
                        val contentStream = ByteArrayContent.fromString("application/json", jsonContent)
                        val checkQuery = "name = '$fileName' and '$taskFolderId' in parents and trashed = false"
                        val existingFile = driveService?.files()?.list()?.setQ(checkQuery)?.execute()?.files?.firstOrNull()
                        if (existingFile != null) {
                            driveService?.files()?.update(existingFile.id, null, contentStream)?.execute()
                        } else {
                            val metadata = com.google.api.services.drive.model.File().apply {
                                name = fileName
                                parents = listOf(taskFolderId)
                            }
                            driveService?.files()?.create(metadata, contentStream)?.execute()
                        }
                    } catch (e: Exception) {
                        Log.e("eura-tasks", "Upload failed for task ${entity.taskUuid}: ${e.message}")
                    }
                }

                val cloudTasks = downloadAllTasks()
                val localActiveTaskUuids = taskDao.getAllTasksByUuidAsc().first().map { it.taskUuid }.toSet()

                cloudTasks.forEach { task ->
                    if (task.taskUuid in allDeletedItemUuids) {
                        deleteTaskFile(
                            type = "task",
                            uuid = task.taskUuid,
                            folderName = "tasks"
                        )
                        Log.d("eura-tasks", "Cloud task file ${task.taskUuid} matched a tombstone. Purging from Drive.")
                        _syncMessage.value = "Cloud task file ${task.taskUuid} matched a tombstone. Purging from Drive."
                    } else if (task.taskUuid !in localActiveTaskUuids) {
                        if (task.parentListId !in localActiveListUuids) {
                            Log.w("eura-tasks", "List '${task.parentListId}' missing for downloaded task. Auto-creating list.")
                            val now = Clock.System.now()
                            listDbViewModel.insertListSynchronously(
                                name = task.parentListId,
                                type = "OTHER",
                                color = "PURPLE",
                                creationTime = now,
                                updateTime = now,
                                uuid = task.parentListId
                            )
                            localActiveListUuids.add(task.parentListId)
                        }
                        taskDbViewModel.insertTask(task)
                        Log.d("eura-tasks", "Downloaded new cloud task: ${task.title}")
                        _syncMessage.value = "Downloaded new cloud task: ${task.title}"
                    }
                }

                val taskTagFolderId = getOrCreateSubFolderId(folderName = "task_tags", parentFolder = getOrCreateMainFolderId() ?: return@launch) ?: return@launch
                val localTaskTags = tagDao.getAllTaskTags().first()
                localTaskTags.forEach { entity ->
                    try {
                        val fileName = "task_tag_${entity.uuid}.json"
                        val syncModel = TaskTagsSyncModel(
                            taskUuid = entity.taskUuid,
                            tagUUid = entity.tagUuid,
                            isActive = entity.isActive,
                            updateTime = entity.updateTime
                        )
                        val jsonContent = gson.toJson(syncModel)
                        val contentStream =
                            ByteArrayContent.fromString("application/json", jsonContent)
                        val checkQuery =
                            "name = '$fileName' and '$taskTagFolderId' in parents and trashed = false"
                        val existingFile = driveService?.files()?.list()?.setQ(checkQuery)
                            ?.execute()?.files?.firstOrNull()
                        if (existingFile != null) {
                            driveService?.files()?.update(existingFile.id, null, contentStream)
                                ?.execute()
                        } else {
                            val metadata = com.google.api.services.drive.model.File().apply {
                                name = fileName
                                parents = listOf(taskTagFolderId)
                            }
                            driveService?.files()?.create(metadata, contentStream)?.execute()
                        }
                    } catch (e: Exception) {
                        Log.e("eura-tasks", "Upload failed for task tag ${entity.uuid}: ${e.message}")
                    }
                }

                val cloudTaskTags = downloadTaskTags()
                val localActiveTaskTags = tagDao.getAllTaskTags().first()

                cloudTaskTags.forEach { tag ->
                    if (!tag.isActive) {
                        deleteTaskFile(
                            type = "task_tag",
                            uuid = tag.uuid,
                            folderName = "task_tags"
                        )
                        Log.d("eura-tasks", "Cloud task tag file ${tag.uuid} matched a tombstone. Purging from Drive.")
                        _syncMessage.value = "Cloud task tag file ${tag.uuid} matched a tombstone. Purging from Drive."
                    } else if (tag.uuid !in localActiveTaskTags.map { it.uuid }) {
                        tagDao.upsertTaskTag(tag)
                        Log.d("eura-tasks", "Downloaded new cloud task tag: ${tag.uuid}")
                        _syncMessage.value = "Downloaded new cloud task tag: ${tag.uuid}"
                    }
                }

                Log.d("eura-tasks", "--- FULL SYNC COMPLETE ---")
                withContext(Dispatchers.Main) {
                    _syncUiState.value = SyncUiState.Success("Sync completed successfully")
                    onComplete()
                }

            } catch (e: Exception) {
                Log.e("eura-tasks", "Sync failed: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _syncUiState.value = SyncUiState.Error("Sync failed: ${e.message}")
                }
            }
        }
    }
}