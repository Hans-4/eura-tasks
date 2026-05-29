package me.hannes.eura_tasks.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import me.hannes.eura_tasks.db.TodoEntity
import me.hannes.eura_tasks.ui.viewModels.GoogleDriveViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLinkGoogleAccount: () -> Unit,
    onClose: () -> Unit,
    driveViewModel: GoogleDriveViewModel = viewModel(),
    localTasks: List<TodoEntity>
) {
    var isSyncing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        driveViewModel.checkExistingLogin(context)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = { onClose() }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding
        ) {
            item {
                Button(
                    onClick = { onLinkGoogleAccount() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Google cloud sync")
                }
            }

            item {
                Button(
                    onClick = {
                        isSyncing = true
                        scope.launch {
                            driveViewModel.syncAllTasks(localTasks)
                            isSyncing = false
                        }
                    },
                    enabled = !isSyncing
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Syncing...")
                    } else {
                        Text("Push Tasks to Google Drive")
                    }
                }
            }
        }
    }
}