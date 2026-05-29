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
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.hannes.eura_tasks.db.TodoEntity
import java.util.Collections
import kotlin.time.Instant

data class TodoSyncModel(
    val uuid: String,
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val isCompleted: Boolean,
    val date: String,
    val time: String,
    val creationTime: Instant
)

class GoogleDriveViewModel : ViewModel() {
    private var driveService: Drive? = null

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

    private suspend fun getOrCreateTasksFolderId(): String? = withContext(Dispatchers.IO) {
        try {
            // 1. Get or Create the MAIN folder ("eura-tasks")
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

            // 2. Get or Create the SUB-folder ("tasks") inside "eura-tasks"
            val subFolderQuery = "name = 'tasks' and '$mainFolderId' in parents and mimeType = 'application/vnd.google-apps.folder' and trashed = false"
            val subResult = driveService?.files()?.list()?.setQ(subFolderQuery)?.execute()
            var subFolderId = subResult?.files?.firstOrNull()?.id

            if (subFolderId == null) {
                val metadata = com.google.api.services.drive.model.File().apply {
                    name = "tasks"
                    mimeType = "application/vnd.google-apps.folder"
                    parents = listOf(mainFolderId!!)
                }
                subFolderId = driveService?.files()?.create(metadata)?.setFields("id")?.execute()?.id
                Log.d("eura-tasks", "Created Sub-Folder: $subFolderId")
            }

            return@withContext subFolderId
        } catch (e: Exception) {
            Log.e("eura-tasks", "Deep folder creation failed: ${e.message}")
            null
        }
    }

    fun syncAllTasks(tasks: List<TodoEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            val folderId = getOrCreateTasksFolderId() ?: return@launch

            tasks.forEach { entity ->
                try {
                    // Filename is now based on the stable UUID
                    val fileName = "task_${entity.uuid}.json"
                    val syncModel = TodoSyncModel(
                        uuid = entity.uuid,
                        title = entity.title,
                        description = entity.description,
                        isFavorite = entity.isFavorite,
                        isCompleted = entity.isCompleted,
                        date = entity.date,
                        time = entity.time,
                        creationTime = entity.creationTime
                    )

                    val gson = GsonBuilder()
                        .registerTypeAdapter(Instant::class.java, com.google.gson.JsonSerializer<Instant> { src, _, _ ->
                            com.google.gson.JsonPrimitive(src.toString())
                        })
                        .create()

                    val jsonContent = gson.toJson(syncModel)

                    val query = "name = '$fileName' and '$folderId' in parents and trashed = false"
                    val existingFile = driveService?.files()?.list()?.setQ(query)?.execute()?.files?.firstOrNull()

                    val contentStream = ByteArrayContent.fromString("application/json", jsonContent)

                    if (existingFile != null) {
                        // Update existing version
                        driveService?.files()?.update(existingFile.id, null, contentStream)?.execute()
                        Log.d("eura-tasks", "Updated: $fileName")
                    } else {
                        // Create new file
                        val metadata = com.google.api.services.drive.model.File().apply {
                            name = fileName
                            parents = listOf(folderId)
                        }
                        driveService?.files()?.create(metadata, contentStream)?.execute()
                        Log.d("eura-tasks", "Created: $fileName")
                    }
                } catch (e: Exception) {
                    Log.e("eura-tasks", "Sync failed for ${entity.uuid}: ${e.message}")
                }
            }
        }
    }
}