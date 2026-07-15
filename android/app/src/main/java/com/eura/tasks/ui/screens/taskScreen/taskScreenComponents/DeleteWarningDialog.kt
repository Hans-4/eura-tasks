package com.eura.tasks.ui.screens.taskScreen.taskScreenComponents

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eura.tasks.R

@Composable
fun DeleteWarningDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        title =  {Text(title)},
        text = {
            Text(text)
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