package me.hannes.eura_tasks.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.hannes.eura_tasks.db.DbEvent
import me.hannes.eura_tasks.ui.UiEvent
import me.hannes.eura_tasks.ui.UiState
import me.hannes.eura_tasks.ui.screens.taskDetailsScreenComponents.DeleteTaskAlertDialog

@Composable
fun TaskDetailsScreen(
    taskId: Int?,
    onClose: () -> Unit,
    onDbEvent: (DbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    uiState: UiState
) {
    val scope = rememberCoroutineScope()

    Scaffold(

    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Button(
                onClick = {
                    onUiEvent(UiEvent.OpenConfirmDeletionDialog)
                }
            ) {
                Text("Delete Task")
            }
        }
    }
    if (uiState.isConfirmingDeletion) {
        DeleteTaskAlertDialog(
            onConfirm = {
                taskId?.let { id ->
                    onDbEvent(DbEvent.DeleteTodoById(id))
                    scope.launch {
                        onUiEvent(UiEvent.CloseConfirmDeletionDialog)
                        delay(300)
                        onClose()
                    }
                }
            },
            onDismiss = { onUiEvent(UiEvent.CloseConfirmDeletionDialog) }
        )
    }
}