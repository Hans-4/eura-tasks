package com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.addTask.addTaskComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShortText
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Sell
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eura.tasks.R
import com.eura.tasks.db.lists.UserListEntity
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagDbState
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.TaskDbState
import com.eura.tasks.db.tasks.repeats.RepeatDbEvent
import com.eura.tasks.db.tasks.repeats.RepeatDbState
import com.eura.tasks.ui.Converter
import com.eura.tasks.ui.SYSTEM_LISTS
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiState
import com.eura.tasks.ui.globalComponents.reminderComponents.ReminderDialogs

@Composable
fun AddTaskScreen(
    onTaskDbEvent: (TaskDbEvent) -> Unit,
    onTagDbEvent: (TagDbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    taskDbState: TaskDbState,
    tagDbState: TagDbState,
    uiState: UiState,
    currentTab: String,
    firstUserTaskList: String,
    onNavigateToSelectTaskListScreen: () -> Unit,
    taskLists: List<UserListEntity>,
    darkTheme: Boolean = isSystemInDarkTheme(),
    onRepeatDbEvent: (RepeatDbEvent) -> Unit,
    repeatDbState: RepeatDbState
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val systemThemeIndex = if (darkTheme) 1 else 0

    val pageList = taskLists.find { it.title == taskDbState.taskParentList.ifBlank { firstUserTaskList } }

    val listTitle = Converter.pageNameConverter(pageName = taskDbState.taskParentList.ifBlank { firstUserTaskList })

    val pageColor = Converter.colorStringConverter(
        systemThemeIndex = systemThemeIndex,
        colorString = pageList?.colorString
    )

    val isFavorite = taskDbState.todoIsFavorite
    val isLockedAsFavorite = currentTab == "SYSTEM_FAVORITES"

    LaunchedEffect(currentTab) {
        if (isLockedAsFavorite) {
            onTaskDbEvent(TaskDbEvent.SetTodoIsFavorite(isFavorite = true, task = null))
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

        val parentList = if (currentTab in SYSTEM_LISTS) {
            listTitle
        } else {
            currentTab
        }


        TextField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth(),
            value = taskDbState.taskTitle,
            onValueChange = {
                onTaskDbEvent(TaskDbEvent.SetTaskTitle(it))
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
                    onTaskDbEvent(TaskDbEvent.SetTodoDescription(it))
                },
                placeholder = {
                    Text(text = "Description")
                }
            )
        }

        if (repeatDbState.toSave) {
            Button(
                onClick = { onUiEvent(UiEvent.OpenAddRepeatsDialog) }
            ) {
                IconButton(
                    onClick = { onRepeatDbEvent(RepeatDbEvent.RemoveToSave) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = null
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
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

            IconButton(
                onClick = {
                    onUiEvent(UiEvent.OpenAddTagsDialog)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Sell,
                    contentDescription = null,
                )
            }

            IconButton(
                onClick = {
                    if (repeatDbState.toSave) {
                        onUiEvent(UiEvent.OpenAddRepeatsDialog)
                    } else {
                        onUiEvent(UiEvent.OpenAddReminderDialog)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Alarm,
                    contentDescription = null,
                )
            }

            IconButton(
                onClick = {
                    onTaskDbEvent(TaskDbEvent.SetTodoIsFavorite(isFavorite = !isFavorite, task = null))
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
                    onTaskDbEvent(TaskDbEvent.SetParentList(parentList))
                    onTaskDbEvent(TaskDbEvent.SaveTask)
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
            onClose = { onUiEvent(UiEvent.CloseAddTagsDialog) },
            onTagDbEvent = onTagDbEvent,
            tagDbState = tagDbState,
            onTaskDbEvent = onTaskDbEvent,
            onUiEvent = onUiEvent,
            uiState = uiState
        )
    }

    ReminderDialogs(
        onUiEvent = onUiEvent,
        uiState = uiState,
        onTaskDbEvent = onTaskDbEvent,
        taskDbState = taskDbState,
        onRepeatDbEvent = onRepeatDbEvent,
        repeatDbState = repeatDbState,

        onDateSelected = { date ->
            onTaskDbEvent(TaskDbEvent.SetTaskDate(date))
            onUiEvent(UiEvent.CloseAddReminderDialog)
        },
    )
}