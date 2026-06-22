package me.hannes.eura_tasks.ui.screens.homeScreenComponents.addTaskBottomSheetComponents

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
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.hannes.eura_tasks.R
import me.hannes.eura_tasks.db.lists.UserListEntity
import me.hannes.eura_tasks.db.tasks.TaskDbEvent
import me.hannes.eura_tasks.db.tasks.TaskDbState
import me.hannes.eura_tasks.ui.Converter
import me.hannes.eura_tasks.ui.SYSTEM_LISTS
import me.hannes.eura_tasks.ui.UiEvent
import me.hannes.eura_tasks.ui.UiState

@Composable
fun AddTaskScreen(
    onDbEvent: (TaskDbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    taskDbState: TaskDbState,
    uiState: UiState,
    currentTab: String,
    firstUserTaskList: String,
    onNavigateToSelectTaskListScreen: () -> Unit,
    taskLists: List<UserListEntity>,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val systemThemeIndex = if (darkTheme) 1 else 0

    val pageList = taskLists.find { it.name == taskDbState.taskParentList.ifBlank { firstUserTaskList } }

    val listTitle = Converter.pageNameConverter(pageName = taskDbState.taskParentList.ifBlank { firstUserTaskList })

    val pageColor = Converter.colorStringConverter(
        systemThemeIndex = systemThemeIndex,
        colorString = pageList?.colorString
    )

    val isFavorite = taskDbState.todoIsFavorite
    val isLockedAsFavorite = currentTab == "SYSTEM_FAVORITES"

    LaunchedEffect(currentTab) {
        if (isLockedAsFavorite) {
            onDbEvent(TaskDbEvent.SetTodoIsFavorite(isFavorite = true, task = null))
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (currentTab in SYSTEM_LISTS) {
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

        val parentList = when {
            currentTab in SYSTEM_LISTS -> taskDbState.taskParentList.ifBlank { firstUserTaskList }
            else -> currentTab
        }

        TextField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth(),
            value = taskDbState.todoTitle,
            onValueChange = {
                onDbEvent(TaskDbEvent.SetTodoTitle(it))
            },
            placeholder = {
                Text(text = "Title")
            },
            singleLine = true,
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
                value = taskDbState.todoDescription,
                onValueChange = {
                    onDbEvent(TaskDbEvent.SetTodoDescription(it))
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

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = {
                    onUiEvent(UiEvent.OpenAddTagsDialog)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tag,
                    contentDescription = null,
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = {
                    onDbEvent(TaskDbEvent.SetTodoIsFavorite(isFavorite = !isFavorite, task = null))
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
                    onDbEvent(TaskDbEvent.SetParentList(parentList))
                    onDbEvent(TaskDbEvent.SaveTask)
                    onUiEvent(UiEvent.CloseAddTaskSheet)
                }
            ) {
                Text(
                    stringResource(R.string.save)
                )
            }
        }
    }

    if (uiState.isAddTagsDialogOpen) {
        AddTagsDialog(
            onDismiss = { onUiEvent(UiEvent.CloseAddTagsDialog) },
            onConfirm = { TODO() }
        )
    }
}