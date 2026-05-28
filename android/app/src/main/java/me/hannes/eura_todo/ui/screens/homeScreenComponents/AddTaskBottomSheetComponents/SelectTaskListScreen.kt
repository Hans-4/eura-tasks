package me.hannes.eura_todo.ui.screens.homeScreenComponents.AddTaskBottomSheetComponents

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
import me.hannes.eura_todo.ui.UiEvent
import me.hannes.eura_todo.ui.theme.blue
import me.hannes.eura_todo.ui.theme.green
import me.hannes.eura_todo.ui.theme.pink
import me.hannes.eura_todo.ui.theme.purple
import me.hannes.eura_todo.ui.theme.red
import me.hannes.eura_todo.ui.theme.yellow
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
    val red = red[systemThemeIndex]
    val yellow = yellow[systemThemeIndex]
    val green = green[systemThemeIndex]
    val blue = blue[systemThemeIndex]
    val purple = purple[systemThemeIndex]
    val pink = pink[systemThemeIndex]

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
            val itemColor = when(item.colorString) {
                "red" -> red
                "yellow" -> yellow
                "green" -> green
                "blue" -> blue
                "purple" -> purple
                "pink" -> pink
                else -> purple
            }

            TextButton(
                onClick = {
                    onDbEvent(DbEvent.SelectTaskList(item.name))
                    onNavigateBackToAddTaskScreen()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    item.name,
                    modifier = Modifier.fillMaxWidth(),
                    color = itemColor.primary
                )
            }
        }
    }
}