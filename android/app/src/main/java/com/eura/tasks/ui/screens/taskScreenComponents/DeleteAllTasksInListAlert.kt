package com.eura.tasks.ui.screens.taskScreenComponents

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eura.tasks.R

@Composable
fun DeleteAllTasksInListAlert(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        title =  {Text("Warning")},
        text = {
            Text("Are you sure to delete this list? This will also delete all Tasks in it.")
        },
        onDismissRequest = {onDismiss()},
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {onDismiss()}
            ) {
                Text(
                    stringResource(R.string.cancel)
                )
            }
        },
    )
}