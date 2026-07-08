package com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.addTask.addTaskComponents

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MoreTime
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eura.tasks.CheckExactAlarmPermission
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.TaskDbState
import com.eura.tasks.openExactAlarmSettings
import com.eura.tasks.openNotificationSettings
import com.eura.tasks.permissionLauncher
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onDateSelected: (Long?) -> Unit,

    onUiEvent: (UiEvent) -> Unit,
    uiState: UiState,

    taskDbEvent: (TaskDbEvent) -> Unit,
    taskDbState: TaskDbState,
) {
    val defaultDate = taskDbState.taskDate ?: remember {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        today.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = defaultDate
    )

    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionLauncher(onUiEvent = onUiEvent)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        CheckExactAlarmPermission(context, onUiEvent)
    }

    LaunchedEffect(Unit) {
        if (taskDbState.taskDate == null) {
            taskDbEvent(TaskDbEvent.SetTaskDate(defaultDate))
        }
    }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier
                .widthIn(max = 380.dp)
                .padding(horizontal = 16.dp)
        ) {
            Column {
                DatePicker(
                    state = datePickerState,
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Button(
                    onClick = { onUiEvent(UiEvent.OpenTimePickDialog) },
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreTime,
                            contentDescription = null
                        )

                        if (taskDbState.taskTimeHour != null && taskDbState.taskTimeMinute != null) {
                            val hour = if (taskDbState.taskTimeHour < 10) "0${taskDbState.taskTimeHour}" else taskDbState.taskTimeHour
                            val minute = if (taskDbState.taskTimeMinute < 10) "0${taskDbState.taskTimeMinute}" else taskDbState.taskTimeMinute

                            Button(
                                onClick = { onUiEvent(UiEvent.OpenTimePickDialog) },
                                shape = MaterialTheme.shapes.small,
                                border = BorderStroke(
                                    width = 1.dp,
                                    brush = SolidColor(MaterialTheme.colorScheme.onSurface)
                                ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                contentPadding = PaddingValues(start = 6.dp, end = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "${hour}:${minute}"
                                    )

                                    IconButton(
                                        onClick = { taskDbEvent(TaskDbEvent.SetTaskTime(null, null)) },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "Add time",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Button(
                    onClick = { onUiEvent(UiEvent.OpenAddRepeatsDialog) },
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Repeat,
                            contentDescription = null
                        )
                        Text(
                            text = "Repeat",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                if (!uiState.isNotificationPermissionGranted) {
                    Text(
                        "Reminders will not work without notification permission",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Button(
                        onClick = { openNotificationSettings(context) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = Color.Blue
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier
                            .height(24.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            "Open settings",
                            fontSize = 14.sp
                        )
                    }
                }

                if (!uiState.isExactAlarmPermissionGranted && uiState.isNotificationPermissionGranted) {
                    Text(
                        "Reminders will not work without exact alarm permission",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Button(
                        onClick = { openExactAlarmSettings(context) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = Color.Blue
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier
                            .height(24.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            "Open settings",
                            fontSize = 14.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { onDismiss() }
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = { onDateSelected(datePickerState.selectedDateMillis) }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}