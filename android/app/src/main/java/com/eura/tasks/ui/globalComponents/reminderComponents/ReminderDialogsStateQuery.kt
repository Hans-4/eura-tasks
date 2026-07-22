package com.eura.tasks.ui.globalComponents.reminderComponents

import androidx.compose.runtime.Composable
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.TaskDbState
import com.eura.tasks.db.tasks.repeats.RepeatDbEvent
import com.eura.tasks.db.tasks.repeats.RepeatDbState
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiState
import com.eura.tasks.ui.globalComponents.reminderComponents.repeatsDialog.AddRepeatsDialog
import com.eura.tasks.ui.globalComponents.reminderComponents.repeatsDialog.components.DatePickDialog

@Composable
        /**
         * Manages and displays all reminder-related dialogs for the app.
         * It handles the state and events for:
         * - [AddReminderDialog]: For setting a one-time reminder for a task.
         * - [TimePickDialog]: For selecting a time (used in repeat reminders).
         * - [AddRepeatsDialog]: For configuring repeat settings (e.g., daily, weekly).
         * - [DatePickDialog]: For selecting start/end dates for repeat reminders.
         *
         * This composable observes [uiState] to determine which dialog to show and dispatches
         */
fun ReminderDialogs(
    onUiEvent: (UiEvent) -> Unit,
    uiState: UiState,

    onTaskDbEvent: (TaskDbEvent) -> Unit,
    taskDbState: TaskDbState,

    onRepeatDbEvent: (RepeatDbEvent) -> Unit,
    repeatDbState: RepeatDbState,

    onDateSelected: (Long?) -> Unit
    ) {
    if (uiState.isAddReminderDialogOpen) {
        AddReminderDialog(
            onDismiss = {
                onUiEvent(UiEvent.CloseAddReminderDialog)
                onTaskDbEvent(TaskDbEvent.SetTaskDate(null))
                onTaskDbEvent(TaskDbEvent.SetTaskTime(null, null))
            },
            onDateSelected = onDateSelected,
            onUiEvent = onUiEvent,
            uiState = uiState,
            taskDbEvent = onTaskDbEvent,
            taskDbState = taskDbState,
        )
    }

    if (uiState.isPickingTime) {
        TimePickDialog(
            onDismiss = { onUiEvent(UiEvent.CloseTimePickDialog) },
            onTimeSelected = { hour, minute ->
                if (uiState.timePickerParentScreen == 1) {
                    onTaskDbEvent(TaskDbEvent.SetTaskTime(hour, minute))
                } else if (uiState.timePickerParentScreen == 2) {
                    onRepeatDbEvent(RepeatDbEvent.SetRepeatTime(hour, minute))
                }
                onUiEvent(UiEvent.CloseTimePickDialog)
            },
            taskState = taskDbState
        )
    }

    if (uiState.isAddingRepeats) {
        AddRepeatsDialog(
            onRepeatDbEvent = onRepeatDbEvent,
            repeatDbState = repeatDbState,
            onUiEvent = onUiEvent,
            onConfirm = {
                onRepeatDbEvent(RepeatDbEvent.SetToSave)

                //Directly return to the bottom sheet
                onUiEvent(UiEvent.CloseAddRepeatsDialog)
                onUiEvent(UiEvent.CloseAddReminderDialog)

                //Remove notification time
                onTaskDbEvent(TaskDbEvent.SetTaskDate(null))
                onTaskDbEvent(TaskDbEvent.SetTaskTime(null, null))
            },
            onDismiss = {
                onUiEvent(UiEvent.CloseAddRepeatsDialog)
            }
        )
    }

    if (uiState.isDatePickDialogOpen) {
        DatePickDialog(
            repeatDbState = repeatDbState,
            onDismiss = { onUiEvent(UiEvent.CloseDatePickDialog) },
            onDateSelected = { date ->
                when (uiState.datePickDialogOpenedFrom) {
                    1 -> onRepeatDbEvent(RepeatDbEvent.SetStartDate(date))
                    2 -> onRepeatDbEvent(RepeatDbEvent.SetEndDate(date))
                }
                onUiEvent(UiEvent.CloseDatePickDialog)
            }
        )
    }
}