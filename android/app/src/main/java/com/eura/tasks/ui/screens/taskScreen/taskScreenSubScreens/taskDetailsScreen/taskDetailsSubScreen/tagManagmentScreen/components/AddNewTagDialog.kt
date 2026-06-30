package com.eura.tasks.ui.screens.taskScreen.taskScreenSubScreens.taskDetailsScreen.taskDetailsSubScreen.tagManagmentScreen.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagDbState

@Composable
fun AddTagDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,

    tagDbState: TagDbState,
    onTagDbEvent: (TagDbEvent) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add new tag") },
        text = {
            TextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth(),
                value = tagDbState.tagTitle,
                onValueChange = {
                    onTagDbEvent(TagDbEvent.SetTagTitle(it))
                },
                placeholder = { Text(text = "Tag name") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm() }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("Cancel")
            }
        }
    )
}