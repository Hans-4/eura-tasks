package me.hannes.eura_tasks.ui.screens

import me.hannes.eura_tasks.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

@Composable
fun TaskDetailsScreen(
    task: TaskEntity,
    onClose: () -> Unit,
    onTaskDbEvent: (TaskDbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    uiState: UiState
) {
    val scope = rememberCoroutineScope()

    Scaffold(

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

            item {
                Column(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    Button(
                        onClick = {
                            onUiEvent(UiEvent.OpenConfirmDeletionDialog)
                        }
                    ) {
                        Text("Delete Task")
                    }
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