package com.eura.tasks.ui.screens.taskScreen.taskScreenSubScreens.taskDetailsScreenComponents

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteTaskAlertDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(
        enabled = true,
        onBack = { onDismiss() }
    )

    AlertDialog(
        title = {
            Text("Do you really wanna delete this Task?")
        },
        text = {
            Text("This action is not undoable")
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}