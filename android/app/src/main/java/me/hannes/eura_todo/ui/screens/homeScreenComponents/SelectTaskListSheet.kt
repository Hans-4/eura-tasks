package me.hannes.eura_todo.ui.screens.homeScreenComponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.hannes.eura_todo.db.DbEvent
import me.hannes.eura_todo.db.DbState
import me.hannes.eura_todo.ui.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTaskListSheet(
    onUiEvent: (UiEvent) -> Unit,
    onDbEvent: (DbEvent) -> Unit,
    dbState: DbState,
    taskLists: List<String>
) {
    ModalBottomSheet(
        onDismissRequest = {onUiEvent(UiEvent.CloseSelectTaskListSheet)},
        dragHandle = null
    ) {
        LazyColumn {
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
}