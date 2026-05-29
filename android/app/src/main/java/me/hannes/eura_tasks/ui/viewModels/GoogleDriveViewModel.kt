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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.hannes.eura_tasks.db.TodoEntity
import java.util.Collections

class GoogleDriveViewModel : ViewModel() {
    private var driveService: Drive? = null

    fun isDriveReady(): Boolean = driveService != null

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

    // 3. Find or Create Folder (returns the ID)
    private suspend fun getOrCreateTasksFolderId(): String? = withContext(Dispatchers.IO) {
        try {
            // Search for folder named 'tasks'
            val query = "name = 'tasks' and mimeType = 'application/vnd.google-apps.folder' and trashed = false"
            val result = driveService?.files()?.list()?.setQ(query)?.setFields("files(id, name)")?.execute()
            val folder = result?.files?.firstOrNull()

            if (folder != null) {
                Log.d("eura-tasks", "Found existing folder: ${folder.id}")
                return@withContext folder.id
            }

            // Create it if not found
            val metadata = com.google.api.services.drive.model.File().apply {
                name = "tasks"
                mimeType = "application/vnd.google-apps.folder"
            }
            val newFolder = driveService?.files()?.create(metadata)?.setFields("id")?.execute()
            Log.d("eura-tasks", "Created NEW folder: ${newFolder?.id}")
            newFolder?.id
        } catch (e: Exception) {
            Log.e("eura-tasks", "Folder Error: ${e.message}")
            null
        }
    }

    // 4. The Sync Function
    fun syncAllTasks(tasks: List<TodoEntity>) {
        if (driveService == null) {
            Log.e("eura-tasks", "Sync failed: driveService is null")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val folderId = getOrCreateTasksFolderId()

            tasks.forEach { entity ->
                try {
                    val fileName = "task_${entity.id}.json"
                    val jsonContent = Gson().toJson(entity)

                    // Search if file exists to update it
                    val query = "name = '$fileName' and '$folderId' in parents and trashed = false"
                    val existingFile = driveService?.files()?.list()?.setQ(query)?.execute()?.files?.firstOrNull()

                    val contentStream = ByteArrayContent.fromString("application/json", jsonContent)

                    if (existingFile != null) {
                        driveService?.files()?.update(existingFile.id, null, contentStream)?.execute()
                        Log.d("eura-tasks", "Updated: $fileName (ID: ${existingFile.id})")
                    } else {
                        val metadata = com.google.api.services.drive.model.File().apply {
                            name = fileName
                            parents = if (folderId != null) listOf(folderId) else null
                        }
                        val newFile = driveService?.files()?.create(metadata, contentStream)?.setFields("id")?.execute()
                        Log.d("eura-tasks", "Created: $fileName (ID: ${newFile?.id})")
                    }
                } catch (e: Exception) {
                    Log.e("eura-tasks", "Failed to sync task ${entity.id}: ${e.message}")
                }
            }
        }
    }
}