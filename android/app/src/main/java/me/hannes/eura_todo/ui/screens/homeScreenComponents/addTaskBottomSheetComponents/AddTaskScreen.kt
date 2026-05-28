package me.hannes.eura_todo.ui.screens.homeScreenComponents.addTaskBottomSheetComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShortText
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.hannes.eura_todo.R
import me.hannes.eura_todo.db.DbEvent
import me.hannes.eura_todo.db.DbState
import me.hannes.eura_todo.ui.Converter
import me.hannes.eura_todo.ui.UiEvent
import me.hannes.eura_todo.ui.UiState
import me.hannes.eura_todo.ui.viewModels.TaskList

@Composable
fun AddTaskScreen(
    onDbEvent: (DbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    dbState: DbState,
    uiState: UiState,
    currentTab: String,
    firstTaskList: String,
    onNavigateToSelectTaskListScreen: () -> Unit,
    taskLists: List<TaskList>,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val systemThemeIndex = if (darkTheme) 1 else 0
    val pageList = taskLists.find { it.name == dbState.taskParentList.ifBlank { firstTaskList } }

    val listTitle = Converter.pageNameConverter(pageName = dbState.taskParentList.ifBlank { firstTaskList })

    val pageColorList = Converter.colorStringConverter(pageList?.colorString)
    val pageColor = pageColorList[systemThemeIndex]

    val isFavorite = dbState.todoIsFavorite
    val isLockedAsFavorite = currentTab == "SYSTEM_FAVORITES"

    LaunchedEffect(currentTab) {
        if (isLockedAsFavorite) {
            onDbEvent(DbEvent.SetTodoIsFavorite(isFavorite = true, todo = null))
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (currentTab == "SYSTEM_FAVORITES" || currentTab == "HOME_SCREEN") {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { onNavigateToSelectTaskListScreen() },
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = pageColor.primary
                )
            ) {
                Text(
                    text = listTitle,
                    color = pageColor.onSurface
                )
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = null,
                    tint = pageColor.onSurface
                )
            }
        }

        val parenList = when (currentTab) {
            "SYSTEM_FAVORITES" -> dbState.taskParentList.ifBlank { firstTaskList }
            "HOME_SCREEN" -> dbState.taskParentList.ifBlank { firstTaskList }
            else -> currentTab
        }

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = dbState.todoTitle,
            onValueChange = {
                onDbEvent(DbEvent.SetTodoTitle(it))
            },
            placeholder = {
                Text(text = "Title")
            },
            singleLine = true
        )

        AnimatedVisibility(
            visible = uiState.isAddingDescription,
            enter = expandVertically(
                animationSpec = tween(300),
                expandFrom = Alignment.Top
            ) + fadeIn(animationSpec = tween(300, delayMillis = 100))
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = dbState.todoDescription,
                onValueChange = {
                    onDbEvent(DbEvent.SetTodoDescription(it))
                },
                placeholder = {
                    Text(text = "Description")
                }
            )
        }

        Row{
            IconButton(
                onClick = {
                    if (uiState.isAddingDescription) {
                        onUiEvent(UiEvent.CloseAddTaskDescriptionTextField)
                    } else {
                        onUiEvent(UiEvent.OpenAddTaskDescriptionTextField)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ShortText,
                    contentDescription = "Add description button"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    onDbEvent(DbEvent.SetTodoIsFavorite(isFavorite = !isFavorite, todo = null))
                },
                enabled = !isLockedAsFavorite
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = null,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = {
                    onDbEvent(DbEvent.SetParentList(parenList))
                    onDbEvent(DbEvent.SaveTask)
                    onUiEvent(UiEvent.CloseAddTaskSheet)
                }
            ) {
                Text(
                    stringResource(R.string.save)
                )
            }
        }
    }
}