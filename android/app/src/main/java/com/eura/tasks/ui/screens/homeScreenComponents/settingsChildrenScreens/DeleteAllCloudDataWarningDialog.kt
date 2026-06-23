package com.eura.tasks.ui.screens.homeScreenComponents.settingsChildrenScreens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteAllCloudDataWarningDialog(
    onClose: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onClose() },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = { onClose() }) {
                Text("Dismiss")
            }
        },
        title = { Text("Warning") },
        text = {
            Text(
                text = "Do you really wanna delete all data in the cloud? This will not affect your local files."
            )
       },
    )
}