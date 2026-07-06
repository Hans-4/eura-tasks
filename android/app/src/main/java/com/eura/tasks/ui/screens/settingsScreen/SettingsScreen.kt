package com.eura.tasks.ui.screens.settingsScreen

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import com.eura.tasks.R
import com.eura.tasks.ui.notifications.Counter
import com.eura.tasks.ui.notifications.CounterNotificationService
import com.eura.tasks.ui.viewModels.GoogleDriveViewModel
import com.eura.tasks.ui.viewModels.ListDbViewModel
import com.eura.tasks.ui.viewModels.SyncUiState
import com.eura.tasks.ui.viewModels.TaskDbViewModel
import kotlin.time.Duration.Companion.milliseconds
import android.provider.Settings
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLinkGoogleAccount: () -> Unit,
    onClose: () -> Unit,
    listDbViewModel: ListDbViewModel,
    taskDbViewModel: TaskDbViewModel,
    googleDriveViewModel: GoogleDriveViewModel,

    service: CounterNotificationService
) {
    val context = LocalContext.current
    val packageName = context.packageName

    val isDriveReady by googleDriveViewModel.isDriveServiceReady.collectAsState()
    val currentContext = LocalContext.current

    var isShowingSyncDetails by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isShowingSyncDetails) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "Arrow Rotation"
    )

    val syncState by googleDriveViewModel.syncUiState.collectAsStateWithLifecycle()
    val syncMessage by googleDriveViewModel.syncMessage.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        googleDriveViewModel.checkExistingLogin(currentContext)
    }

    LaunchedEffect(syncState) {
        if (syncState is SyncUiState.Error) {
            delay(5000.milliseconds)

            googleDriveViewModel.resetSyncStatus()
        }
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
        },
        bottomBar = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = { isShowingSyncDetails = !isShowingSyncDetails },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropUp,
                        contentDescription = null,
                        modifier = Modifier
                            .rotate(rotation)
                            .size(26.dp)
                    )
                }

                AnimatedVisibility(
                    visible = isShowingSyncDetails,
                    enter = expandVertically(
                        animationSpec = tween(300),
                        expandFrom = Alignment.Top
                    ) + fadeIn(animationSpec = tween(300, delayMillis = 100))
                ) {
                    Text(text = if (isDriveReady) syncMessage else "Not connected to Google Drive")
                }

                Button(
                    onClick = {
                        googleDriveViewModel.startFullSync(
                            taskDbViewModel = taskDbViewModel,
                            listDbViewModel = listDbViewModel,
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = MaterialTheme.shapes.medium,
                    enabled = syncState !is SyncUiState.Loading && isDriveReady,
                    border = BorderStroke(
                        width = 1.dp,
                        brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (syncState is SyncUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Syncing...")
                    } else if (!isDriveReady) {
                        Column{
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxSize(),
            contentPadding = innerPadding
        ) {
            item {
                Button(
                    onClick = { onLinkGoogleAccount() },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Synchronisation",
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        val intent = Intent().apply {
                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        }

                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            try {
                                val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", packageName, null)
                                }
                                context.startActivity(fallbackIntent)
                            } catch (anr: Exception) {
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Notifications",
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Button(onClick = {
                    service.showNotification(Counter.value)
                }) {
                    Text("Show notification")
                }
            }
        }
    }
}