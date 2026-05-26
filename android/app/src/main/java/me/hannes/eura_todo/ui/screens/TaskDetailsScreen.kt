package me.hannes.eura_todo.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.hannes.eura_todo.db.DbEvent
import me.hannes.eura_todo.ui.UiEvent
import me.hannes.eura_todo.ui.UiState
import me.hannes.eura_todo.ui.screens.homeScreenComponents.TaskDetailsScreenComponents.DeleteTaskAlertDialog

@Composable
fun TaskDetailsScreen(
    taskId: Int?,
    onClose: () -> Unit,
    onDbEvent: (DbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    uiState: UiState
) {
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
            onClose = onClose,
            onDbEvent = onDbEvent,
            taskId = taskId,
            onUiEvent = onUiEvent
        )
    }
}