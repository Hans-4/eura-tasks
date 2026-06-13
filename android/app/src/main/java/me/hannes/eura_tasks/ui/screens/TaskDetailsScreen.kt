package me.hannes.eura_tasks.ui.screens

import androidx.activity.compose.BackHandler
import me.hannes.eura_tasks.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.List
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.hannes.eura_tasks.db.tasks.TaskDbEvent
import me.hannes.eura_tasks.db.tasks.TaskEntity
import me.hannes.eura_tasks.ui.UiEvent
import me.hannes.eura_tasks.ui.UiState
import me.hannes.eura_tasks.ui.screens.taskDetailsScreenComponents.DeleteTaskAlertDialog
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
) {
    val scope = rememberCoroutineScope()

    BackHandler(
        enabled = true,
        onBack = { onClose() }
    )


    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onClose() }) {
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
                        onClick = { onTaskList(task.taskList) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.List,
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
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
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
                            selected = task.isCompleted
                        )

                        BasicTextField(
                            value = task.title,
                            onValueChange = {
                                onTaskDbEvent(
                                    TaskDbEvent.UpdateTaskTitleById(
                                        id = task.id,
                                        newTitle = it
                                    )
                                )
                            },
                            textStyle = TextStyle.Default.copy(
                                fontSize = 16.sp,
                            )
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(
                            onClick = {
                                onTaskDbEvent(TaskDbEvent.SetTodoIsFavorite(!task.isFavorite, task))
                            }
                        ) {
                            Icon(
                                imageVector = if (task.isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                contentDescription = "Toggle favorite",
                            )
                        }
                    }
                }
            }

            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
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
                        textStyle = TextStyle.Default.copy(
                            fontSize = 16.sp,
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Text(
                                    text = if (task.description.isEmpty()) stringResource(R.string.add_description) else "",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    fontSize = 16.sp,
                                )
                                innerTextField()
                            }
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }

            }
        }
    }
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