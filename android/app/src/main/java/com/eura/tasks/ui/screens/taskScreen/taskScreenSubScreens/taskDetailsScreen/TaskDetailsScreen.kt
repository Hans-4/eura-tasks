package com.eura.tasks.ui.screens.taskScreen.taskScreenSubScreens.taskDetailsScreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eura.tasks.R
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagDbState
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.TaskDbState
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.db.tasks.repeats.RepeatDbEvent
import com.eura.tasks.db.tasks.repeats.RepeatDbState
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiState
import com.eura.tasks.ui.globalComponents.reminderComponents.ReminderDialogs
import com.eura.tasks.ui.screens.taskScreen.taskScreenSubScreens.taskDetailsScreen.components.DeleteTaskAlertDialog
import com.eura.tasks.ui.viewModels.TypeConverter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    parentScreen: String,

    task: TaskEntity,
    onClose: () -> Unit,

    onTaskDbEvent: (TaskDbEvent) -> Unit,

    onUiEvent: (UiEvent) -> Unit,
    uiState: UiState,
    onTaskList: (String) -> Unit,

    onTagDbEvent: (TagDbEvent) -> Unit,
    tagDbState: TagDbState,

    taskDbState: TaskDbState,

    onRepeatDbEvent: (RepeatDbEvent) -> Unit,
    repeatDbState: RepeatDbState,


    onTagManagement: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val taskTags = tagDbState.tagsFromCurrentTask

    BackHandler(
        enabled = true,
        onBack = { onClose() }
    )

    LaunchedEffect(taskTags) {
        onTagDbEvent(TagDbEvent.GetAllTagsByUuid(task.uuid))
    }

    LaunchedEffect(Unit) {
        onTaskDbEvent(TaskDbEvent.SetTaskTitle(task.title))
    }


    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // Saves the current title when exiting the screen to improve performance and prevent blank title
                            val taskTitle = taskDbState.taskTitle
                            if (taskTitle.isNotBlank()) {
                                onTaskDbEvent(TaskDbEvent.UpdateTaskTitleById(task.id, taskTitle))
                            }
                            onClose()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Text(
                        text = if (parentScreen == "search") stringResource(R.string.search) else parentScreen
                    )
                },
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
            ) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Button(
                        onClick = {
                            if (task.taskList != parentScreen) {
                                onTaskList(task.taskList)
                            } else {
                                onClose()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.List,
                            contentDescription = null
                        )
                        Text("${stringResource(R.string.open)} '${task.taskList}'")
                    }

                    IconButton(
                        onClick = { onUiEvent(UiEvent.OpenConfirmDeletionDialog) },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                    ) {
                        TitleCard(
                            onTaskDbEvent = onTaskDbEvent,
                            taskDbState = taskDbState,
                            task = task
                        )

                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        TimeCard(
                            onReminderManagement = { onUiEvent(UiEvent.OpenAddReminderDialog) },
                            onRemoveDateTime = { onTaskDbEvent(TaskDbEvent.UpdateTaskDateTime(task.id, null))},
                            task = task
                        )

                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        TagButton(
                            taskTags = taskTags,
                            onTagManagement = onTagManagement
                        )

                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        DescriptionCard(
                            task = task,
                            onTaskDbEvent = onTaskDbEvent
                        )
                    }
                }
            }
        }
    }

    ReminderDialogs(
        onUiEvent = onUiEvent,
        uiState = uiState,
        onTaskDbEvent = onTaskDbEvent,
        taskDbState = taskDbState,
        onRepeatDbEvent = onRepeatDbEvent,
        repeatDbState = repeatDbState,

        onDateSelected = { date ->
            onTaskDbEvent(TaskDbEvent.UpdateTaskDateTime(task.id, date))
            onUiEvent(UiEvent.CloseAddReminderDialog)
        },
    )

    if (uiState.isConfirmingDeletion) {
        DeleteTaskAlertDialog(
            onConfirm = {
                task.let {
                    onTaskDbEvent(TaskDbEvent.DeleteTodoById(task.id))
                    scope.launch {
                        onUiEvent(UiEvent.CloseConfirmDeletionDialog)
                        delay(300.milliseconds)
                        onClose()
                    }
                }
            },
            onDismiss = { onUiEvent(UiEvent.CloseConfirmDeletionDialog) }
        )
    }
}

@Composable
fun TagDisplayCard(name: String) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
            )

            Text(
                text = name
            )
        }
    }
}

@Composable
fun TitleCard(
    task: TaskEntity,
    onTaskDbEvent: (TaskDbEvent) -> Unit,
    taskDbState: TaskDbState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            onClick = {
                onTaskDbEvent(
                    TaskDbEvent.SetIsCompleted(
                        isCompleted = !task.isCompleted,
                        task = task
                    )
                )
            },
            selected = task.isCompleted,
        )

        BasicTextField(
            value = taskDbState.taskTitle,
            onValueChange = {
                onTaskDbEvent(
                    TaskDbEvent.SetTaskTitle(title = it)
                )
            },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
            ),
            modifier = Modifier.widthIn(min = 128.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = {
                onTaskDbEvent(
                    TaskDbEvent.SetTodoIsFavorite(
                        !task.isFavorite,
                        task
                    )
                )
            },
        ) {
            Icon(
                imageVector = if (task.isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                contentDescription = "Toggle favorite",
            )
        }
    }
}

@Composable
fun TagButton(
    taskTags: List<TagsEntity>,
    onTagManagement: () -> Unit
) {
    Button(
        onClick = { onTagManagement() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Sell,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                if (taskTags.isEmpty()) {
                    Text(
                        "No tags",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    taskTags
                        .take(2)
                        .forEach { item ->
                            TagDisplayCard(
                                name = item.title
                            )
                        }
                }

                if (taskTags.size > 2) {
                    val tagCount = taskTags.size - 2
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.extraLarge,
                    ) {
                        Text(
                            text = "+${tagCount}",
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun TimeCard(
    onReminderManagement: () -> Unit,
    onRemoveDateTime: () -> Unit,

    task: TaskEntity
) {
    val converter = TypeConverter()
    val dueDateTimeString = if (task.dueDateTime != null) converter.formatInstant(task.dueDateTime) else "No reminder"

    Button(
        onClick = { onReminderManagement() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        contentPadding = PaddingValues(16.dp, 8.dp, 0.dp, 8.dp),
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Alarm,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = dueDateTimeString,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = { onRemoveDateTime() }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun DescriptionCard(
    task: TaskEntity,
    onTaskDbEvent: (TaskDbEvent) -> Unit
) {
    BasicTextField(
        value = task.description,
        onValueChange = {
            onTaskDbEvent(
                TaskDbEvent.UpdateDescriptionById(
                    id = task.id,
                    newDescription = it
                )
            )
        },
        maxLines = 15,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 16.sp,
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopStart,
            ) {
                Text(
                    text = if (task.description.isEmpty()) stringResource(R.string.add_description) else "",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.6f
                    ),
                    fontSize = 16.sp,
                )
                innerTextField()
            }
        },
        modifier = Modifier
            .heightIn(min = 96.dp)
            .padding(16.dp)
    )
}