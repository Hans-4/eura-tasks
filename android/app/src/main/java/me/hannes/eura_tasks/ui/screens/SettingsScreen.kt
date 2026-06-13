package me.hannes.eura_tasks.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import me.hannes.eura_tasks.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.hannes.eura_tasks.db.lists.ListDbState
import me.hannes.eura_tasks.ui.viewModels.TaskDbViewModel
import me.hannes.eura_tasks.ui.viewModels.GoogleDriveViewModel
import me.hannes.eura_tasks.ui.viewModels.ListDbViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLinkGoogleAccount: () -> Unit,
    onClose: () -> Unit,
    listDbState: ListDbState,
    listDbViewModel: ListDbViewModel,
    taskDbViewModel: TaskDbViewModel,
    googleDriveViewModel: GoogleDriveViewModel,
) {
    var isSyncing by remember { mutableStateOf(false) }
    val isDriveReady by googleDriveViewModel.isDriveServiceReady.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        googleDriveViewModel.checkExistingLogin(context)
    }

    BackHandler(
        enabled = true,
        onBack = { onClose() }
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.settings))
                },
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
                    Text("Manage Google cloud sync")
                }
            }

            item {
                Button(
                    onClick = {
                        isSyncing = true
                        googleDriveViewModel.startFullSync(
                            taskDbViewModel = taskDbViewModel,
                            listDbViewModel = listDbViewModel,
                            onComplete = {
                                isSyncing = false
                                Log.d("eura-tasks", "Two-way sync complete!")
                            }
                        )
                    },
                    enabled = !isSyncing && isDriveReady,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Syncing...")
                    } else if (!isDriveReady) {
                        Column(

                        ) {
                            Text(
                                text = "Sync with Google Drive",
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Connect your Google account first",
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        Text("Sync with Google Drive")
                    }
                }
            }
        }
    }
}