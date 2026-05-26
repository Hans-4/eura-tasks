package me.hannes.eura_todo.ui.screens.homeScreenComponents.AddTaskBottomSheetComponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.hannes.eura_todo.db.DbEvent
import me.hannes.eura_todo.ui.UiEvent

@Composable
fun SelectTaskListScreen(
    onDbEvent: (DbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    taskLists: List<String>,
    onNavigateBackToAddTaskScreen: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        item {
            IconButton(
                onClick = {onNavigateBackToAddTaskScreen()}
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "navigate back"
                )
            }
        }
        items(taskLists) { item ->
            TextButton(
                onClick = {
                    onDbEvent(DbEvent.SelectTaskList(item))
                    onUiEvent(UiEvent.CloseSelectTaskListSheet)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    item,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}