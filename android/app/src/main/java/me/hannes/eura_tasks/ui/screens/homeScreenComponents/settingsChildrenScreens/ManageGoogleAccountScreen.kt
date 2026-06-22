package me.hannes.eura_tasks.ui.screens.homeScreenComponents.settingsChildrenScreens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import me.hannes.eura_tasks.ui.SYNC_INTERVAL_OPTIONS
import me.hannes.eura_tasks.ui.UiEvent
import me.hannes.eura_tasks.ui.UiState
import me.hannes.eura_tasks.ui.viewModels.GoogleDriveViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkGoogleAccountScreen(
    googleDriveViewModel: GoogleDriveViewModel,
    onUiEvent: (UiEvent) -> Unit,
    uiState: UiState,
    onSuccess: (GoogleSignInAccount) -> Unit,
    onSignOut: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current

    // Keep track of whether the user is currently signed in
    var signedInAccount by remember {
        mutableStateOf(GoogleSignIn.getLastSignedInAccount(context))
    }

    // 1. Configure Sign-In
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // 2. The Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                signedInAccount = account // Update local UI state
                onSuccess(account) // Pass the account to your Drive logic
            }
        } catch (e: ApiException) {
            Log.e("EuraTasks", "Login Failed: ${e.statusCode}")
        }
    }

    var isManageIntervalsDialogOpen by remember { mutableStateOf(false) }
    var selectedInterval by remember { mutableIntStateOf(0) }

    BackHandler(
        enabled = true,
        onBack = { onClose() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Google Account") },
                navigationIcon = {
                    IconButton(onClick = { onClose() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (signedInAccount != null) {
                // User is Signed In -> Show Account Info & Log Out Button
                Text(
                    text = "Linked as: ${signedInAccount?.email}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { isManageIntervalsDialogOpen = true }
                ) {
                    Text(
                        text = "synchronisation intervals: ${SYNC_INTERVAL_OPTIONS[selectedInterval]}"
                    )
                }

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = {
                        googleSignInClient.signOut().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signedInAccount = null
                                onSignOut()
                            } else {
                                Log.e("EuraTasks", "Sign out failed.")
                            }
                        }
                    }
                ) {
                    Text("Disconnect Account")
                }

                Button(
                    onClick = { onUiEvent(UiEvent.OpenDeleteAllCloudDataWarningDialog) }
                ) {
                    Text("Delete all cloud data")
                }

            } else {
                Button(onClick = { launcher.launch(googleSignInClient.signInIntent) }) {
                    Text("Sign in with Google")
                }
            }
        }
    }

    if (uiState.isDeleteAllCloudDataWarningDialogOpen) {
        DeleteAllCloudDataWarningDialog(
            onClose = { onUiEvent(UiEvent.CloseDeleteAllCloudDataWarningDialog) },
            onConfirm = {
                onUiEvent(UiEvent.CloseDeleteAllCloudDataWarningDialog)
                googleDriveViewModel.deleteAllData()
            }
        )
    }

    if (isManageIntervalsDialogOpen) {
        ManageSyncIntervalsDialog(
            onConfirm = { selectedIndex ->
                isManageIntervalsDialogOpen = false
                selectedInterval = selectedIndex
            },
        )
    }
}