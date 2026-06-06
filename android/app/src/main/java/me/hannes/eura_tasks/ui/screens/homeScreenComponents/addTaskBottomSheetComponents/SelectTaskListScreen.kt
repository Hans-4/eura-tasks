package me.hannes.eura_tasks.ui.screens.homeScreenComponents.addTaskBottomSheetComponents

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
import me.hannes.eura_tasks.db.lists.UserListEntity
import me.hannes.eura_tasks.db.tasks.TaskDbEvent
import me.hannes.eura_tasks.ui.Converter
import me.hannes.eura_tasks.ui.UiEvent

@Composable
fun SelectTaskListScreen(
    onDbEvent: (TaskDbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    taskLists: List<UserListEntity>,
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

            val itemColor = Converter.colorStringConverter(
                systemThemeIndex = systemThemeIndex,
                colorString = item.colorString
            )

            TextButton(
                onClick = {
                    onDbEvent(TaskDbEvent.SelectTaskList(item.name))
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