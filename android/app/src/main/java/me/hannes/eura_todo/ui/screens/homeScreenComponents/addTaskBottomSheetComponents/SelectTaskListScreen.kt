package me.hannes.eura_todo.ui.screens.homeScreenComponents.addTaskBottomSheetComponents

import androidx.compose.foundation.isSystemInDarkTheme
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
import me.hannes.eura_todo.ui.Converter
import me.hannes.eura_todo.ui.UiEvent
import me.hannes.eura_todo.ui.viewModels.TaskList

@Composable
fun SelectTaskListScreen(
    onDbEvent: (DbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    taskLists: List<TaskList>,
    onNavigateBackToAddTaskScreen: () -> Unit,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val systemThemeIndex = if (darkTheme) 1 else 0

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
        items(taskLists.drop(6)) { item ->
            val listTitle = Converter.pageNameConverter(pageName = item.name)

            val itemColorList = Converter.colorStringConverter(item.colorString)
            val itemColor = itemColorList[systemThemeIndex]

            TextButton(
                onClick = {
                    onDbEvent(DbEvent.SelectTaskList(item.name))
                    onNavigateBackToAddTaskScreen()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    listTitle,
                    modifier = Modifier.fillMaxWidth(),
                    color = itemColor.primary
                )
            }
        }
    }
}