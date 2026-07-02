package com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.addTask.addTaskComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.MoreTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    // 1. Initialize the state with the current time
    val calendar = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = false // Set to true if you want 24h format
    )

    // 2. Track whether we are showing the Dial (1) or the Keyboard Input (2)
    var showKeyboardInput by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {},
        dismissButton = {},
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (showKeyboardInput) "Enter time" else "Select time",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                )

                if (showKeyboardInput) {
                    TimeInput(state = timePickerState)
                } else {
                    TimePicker(state = timePickerState)
                }

                Spacer(modifier = Modifier.padding(top = 10.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { showKeyboardInput = !showKeyboardInput }
                    ) {
                        if (showKeyboardInput) {
                            Icon(
                                imageVector = Icons.Rounded.MoreTime,
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Keyboard,
                                contentDescription = null
                            )
                        }
                    }

                    Row(

                    ) {
                        TextButton(
                            onClick = onDismiss
                        ) {
                            Text("Cancel")
                        }

                        TextButton(
                            onClick = {
                                onTimeSelected(timePickerState.hour, timePickerState.minute)
                            }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    )
}