package com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.addTask.addTaskComponents.repeatsDialog.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import com.eura.tasks.db.tasks.repeats.RepeatDbState

@Composable
fun DatePickDialog(
    repeatDbState: RepeatDbState,

    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = repeatDbState.startDate
    )

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onDateSelected(datePickerState.selectedDateMillis!!) }
            ) {
                Text("Save")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}