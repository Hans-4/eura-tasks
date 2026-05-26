package me.hannes.eura_todo.ui.screens.homeScreenComponents.TaskDetailsScreenComponents

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import me.hannes.eura_todo.db.DbEvent
import me.hannes.eura_todo.ui.UiEvent

@Composable
fun DeleteTaskAlertDialog(
    onClose: () -> Unit,
    onDbEvent: (DbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    taskId: Int?
) {
    AlertDialog(
        title = {
            Text("Do you really wanna delete this Task?")
        },
        text = {
            Text("This action is not undoable")
        },
        onDismissRequest = {onUiEvent(UiEvent.CloseConfirmDeletionDialog)},
        confirmButton = {
            TextButton(
                onClick = {
                    taskId?.let { id ->
                        onUiEvent(UiEvent.CloseConfirmDeletionDialog)
                        onDbEvent(DbEvent.DeleteTodoById(id))
                        onClose()
                    }
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onUiEvent(UiEvent.CloseConfirmDeletionDialog)
                }
            ) {
                Text("Cancel")
            }
        }
    )
}