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

class GoogleDriveViewModel : ViewModel() {
    private var driveService: Drive? = null

    // Consistent GSON for all methods
    private val gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, com.google.gson.JsonSerializer<Instant> { src, _, _ ->
            com.google.gson.JsonPrimitive(src.toString())
        })
        .registerTypeAdapter(Instant::class.java, com.google.gson.JsonDeserializer { json, _, _ ->
            Instant.parse(json.asString)
        })
        .create()

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

    fun checkExistingLogin(context: Context) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            initDriveService(context, account)
        }
    }

    /**
     * Helper to retrieve or create the root "eura-tasks" folder ID
     */
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

    /**
     * Resolves the folder ID for the "tasks" subfolder
     */
    private suspend fun getOrCreateTasksFolderId(): String? = withContext(Dispatchers.IO) {
        val mainFolderId = getOrCreateMainFolderId() ?: return@withContext null
        try {
            val subFolderQuery = "name = 'tasks' and '$mainFolderId' in parents and mimeType = 'application/vnd.google-apps.folder' and trashed = false"
            val subResult = driveService?.files()?.list()?.setQ(subFolderQuery)?.execute()
            var subFolderId = subResult?.files?.firstOrNull()?.id

            if (subFolderId == null) {
                val metadata = com.google.api.services.drive.model.File().apply {
                    name = "tasks"
                    mimeType = "application/vnd.google-apps.folder"
                    parents = listOf(mainFolderId)
                }
                subFolderId = driveService?.files()?.create(metadata)?.setFields("id")?.execute()?.id
                Log.d("eura-tasks", "Created Tasks Sub-Folder: $subFolderId")
            }
            return@withContext subFolderId
        } catch (e: Exception) {
            Log.e("eura-tasks", "Tasks folder creation failed: ${e.message}")
            null
        }
    }

    /**
     * NEW: Resolves the folder ID for the "lists" subfolder
     */
    private suspend fun getOrCreateListsFolderId(): String? = withContext(Dispatchers.IO) {
        val mainFolderId = getOrCreateMainFolderId() ?: return@withContext null
        try {
            val subFolderQuery = "name = 'lists' and '$mainFolderId' in parents and mimeType = 'application/vnd.google-apps.folder' and trashed = false"
            val subResult = driveService?.files()?.list()?.setQ(subFolderQuery)?.execute()
            var subFolderId = subResult?.files?.firstOrNull()?.id

            if (subFolderId == null) {
                val metadata = com.google.api.services.drive.model.File().apply {
                    name = "lists"
                    mimeType = "application/vnd.google-apps.folder"
                    parents = listOf(mainFolderId)
                }
                subFolderId = driveService?.files()?.create(metadata)?.setFields("id")?.execute()?.id
                Log.d("eura-tasks", "Created Lists Sub-Folder: $subFolderId")
            }
            return@withContext subFolderId
        } catch (e: Exception) {
            Log.e("eura-tasks", "Lists folder creation failed: ${e.message}")
            null
        }
    }

    /**
     * Downloads all tasks from Google Drive and converts them to TodoEntity
     */
    suspend fun downloadAllTasks(): List<TodoEntity> = withContext(Dispatchers.IO) {
        val downloadedTasks = mutableListOf<TodoEntity>()
        if (driveService == null) {
            Log.e("eura-tasks", "Drive Service not initialized")
            return@withContext emptyList()
        }

        try {
            val folderId = getOrCreateTasksFolderId() ?: return@withContext emptyList()

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
     * Two-way sync: Downloads all tasks from cloud, and uploads local tasks to cloud.
     */
    fun syncWithDatabase(localTasks: List<TodoEntity>, onComplete: (List<TodoEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val folderId = getOrCreateTasksFolderId() ?: return@launch

            // --- STEP 1: DOWNLOAD (PULL) ---
            val downloadedTasks = downloadAllTasks()

            // --- STEP 2: UPLOAD (PUSH) ---
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
                    val contentStream = ByteArrayContent.fromString("application/json", jsonContent)

                    val checkQuery = "name = '$fileName' and '$folderId' in parents and trashed = false"
                    val existingFile = driveService?.files()?.list()?.setQ(checkQuery)?.execute()?.files?.firstOrNull()

                    if (existingFile != null) {
                        driveService?.files()?.update(existingFile.id, null, contentStream)?.execute()
                        Log.d("eura-tasks", "Updated: $fileName")
                    } else {
                        val metadata = com.google.api.services.drive.model.File().apply {
                            name = fileName
                            parents = listOf(folderId)
                        }
                        driveService?.files()?.create(metadata, contentStream)?.execute()
                        Log.d("eura-tasks", "Created: $fileName")
                    }
                } catch (e: Exception) {
                    Log.e("eura-tasks", "Upload failed for ${entity.uuid}: ${e.message}")
                }
            }

            withContext(Dispatchers.Main) {
                onComplete(downloadedTasks)
            }
        }
    }

    fun deleteTaskFile(uuid: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            if (driveService == null) {
                Log.e("eura-tasks", "Drive Service not initialized")
                withContext(Dispatchers.Main) { onResult(false) }
                return@launch
            }

            try {
                val folderId = getOrCreateTasksFolderId()
                if (folderId == null) {
                    withContext(Dispatchers.Main) { onResult(false) }
                    return@launch
                }

                val fileName = "task_${uuid}.json"
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
     * Downloads the single JSON file tracking custom application sublists from the 'lists' folder.
     */
    suspend fun downloadUserLists(): List<TaskList> = withContext(Dispatchers.IO) {
        if (driveService == null) {
            Log.e("eura-tasks", "Drive Service not initialized")
            return@withContext emptyList()
        }

        try {
            // TARGETING: lists folder
            val folderId = getOrCreateListsFolderId() ?: return@withContext emptyList()
            val query = "name = 'lists.json' and '$folderId' in parents and mimeType = 'application/json' and trashed = false"
            val existingFile = driveService?.files()?.list()?.setQ(query)?.execute()?.files?.firstOrNull()

            if (existingFile != null) {
                val outputStream = java.io.ByteArrayOutputStream()
                driveService?.files()?.get(existingFile.id)?.executeMediaAndDownloadTo(outputStream)

                val jsonString = outputStream.toString()
                val typeToken = object : TypeToken<List<TaskList>>() {}.type
                return@withContext gson.fromJson<List<TaskList>>(jsonString, typeToken) ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("eura-tasks", "Failed to download custom task lists file: ${e.message}")
        }
        return@withContext emptyList()
    }

    /**
     * Two-way sync wrapper for user metadata configuration lists inside the 'lists' folder.
     */
    fun syncUserLists(localLists: List<TaskList>, onComplete: (List<TaskList>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            // TARGETING: lists folder
            val folderId = getOrCreateListsFolderId() ?: return@launch

            // 1. Download Remote Data
            val remoteLists = downloadUserLists()

            // 2. Serialize and Upload Local State Overwriting Remote
            try {
                val jsonContent = gson.toJson(localLists)
                val contentStream = ByteArrayContent.fromString("application/json", jsonContent)

                val query = "name = 'lists.json' and '$folderId' in parents and trashed = false"
                val existingFile = driveService?.files()?.list()?.setQ(query)?.execute()?.files?.firstOrNull()

                if (existingFile != null) {
                    driveService?.files()?.update(existingFile.id, null, contentStream)?.execute()
                    Log.d("eura-tasks", "Updated lists.json on cloud storage container.")
                } else {
                    val metadata = com.google.api.services.drive.model.File().apply {
                        name = "lists.json"
                        parents = listOf(folderId)
                    }
                    driveService?.files()?.create(metadata, contentStream)?.execute()
                    Log.d("eura-tasks", "Created lists.json configuration schema mapping.")
                }
            } catch (e: Exception) {
                Log.e("eura-tasks", "Failed uploading local updates back to cloud configurations: ${e.message}")
            }

            withContext(Dispatchers.Main) {
                onComplete(remoteLists)
            }
        }
    }
}