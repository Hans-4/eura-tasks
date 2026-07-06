package com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.addTask.addTaskComponents.repeatsDialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eura.tasks.db.tasks.repeats.RepeatDbEvent
import com.eura.tasks.db.tasks.repeats.RepeatDbState
import com.eura.tasks.ui.UiEvent
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRepeatsDialog(
    onRepeatDbEvent: (RepeatDbEvent) -> Unit,
    repeatDbState: RepeatDbState,

    onUiEvent: (UiEvent) -> Unit,

    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("1") }

    var endText by remember { mutableStateOf("10") }

    val maxCharacter = 2

    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Day", "Week", "Month", "Year")

    LaunchedEffect(repeatDbState.startDate) {
        if (repeatDbState.startDate == null) {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            val pureDateTimestamp = today.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

            onRepeatDbEvent(RepeatDbEvent.SetStartDate(pureDateTimestamp))
        }
    }

    LaunchedEffect(repeatDbState.endDate) {
        if (repeatDbState.endDate == null) {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            val tomorrow = today.plus(1, DateTimeUnit.DAY)

            val pureDateTimestamp = tomorrow.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

            onRepeatDbEvent(RepeatDbEvent.SetEndDate(pureDateTimestamp))
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { onDismiss() }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Back"
                            )
                        }
                    },
                    title = { Text("Repeats") },
                    actions = {
                        TextButton(
                            onClick = { TODO() }
                        ) {
                            Text("Save")
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Every"
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            BasicTextField(
                                value = repeatDbState.repeatEvery,
                                onValueChange = {
                                    if (it.length <= maxCharacter) onRepeatDbEvent(RepeatDbEvent.SetRepeatIntervals(it))
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier,
                                decorationBox = { innerTextField ->
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .width(56.dp)
                                            .height(56.dp)
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.outlineVariant,
                                                shape = MaterialTheme.shapes.small
                                            ),
                                    ) {
                                        innerTextField()
                                    }
                                }
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentSize(Alignment.TopStart)
                            ) {
                                Button(
                                    onClick = { expanded = true },
                                    shape = MaterialTheme.shapes.small,
                                    colors = MaterialTheme.colorScheme.run {
                                        ButtonDefaults.buttonColors(
                                            containerColor = surface,
                                            contentColor = onSurfaceVariant
                                        )
                                    },
                                    border = BorderStroke(
                                        width = 1.dp,
                                        brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                                    ),
                                    contentPadding = PaddingValues(8.dp),
                                    modifier = Modifier
                                        .height(56.dp)
                                        .fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            options[repeatDbState.selectedRepeatType],
                                        )

                                        Icon(
                                            imageVector = Icons.Rounded.ArrowDropDown,
                                            contentDescription = null
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    options.forEachIndexed { index, text ->
                                        DropdownMenuItem(
                                            text = { Text(text) },
                                            onClick = {
                                                onRepeatDbEvent(RepeatDbEvent.SetSelectedRepeatType(index))
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { onUiEvent(UiEvent.OpenTimePickDialog) },
                            shape = MaterialTheme.shapes.small,
                            colors = MaterialTheme.colorScheme.run {
                                ButtonDefaults.buttonColors(
                                    containerColor = surface,
                                    contentColor = onSurfaceVariant
                                )
                            },
                            border = BorderStroke(
                                width = 1.dp,
                                brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                            ),
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier
                                .height(56.dp)
                                .fillMaxWidth()
                        ) {
                            if (repeatDbState.repeatTimeHour == null && repeatDbState.repeatTimeMinute == null) {
                                Text(
                                    text = "Set time",
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                val hour = repeatDbState.repeatTimeHour ?: "00"
                                val minute = repeatDbState.repeatTimeMinute ?: "00"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${hour}:${minute}",
                                    )

                                    IconButton(
                                        onClick = { onRepeatDbEvent(RepeatDbEvent.SetRepeatTime(null, null)) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Column(

                    ) {
                        Text(
                            "Starts"
                        )

                        Button(
                            onClick = { onUiEvent(UiEvent.OpenDatePickDialog(1)) },
                            shape = MaterialTheme.shapes.small,
                            colors = MaterialTheme.colorScheme.run {
                                ButtonDefaults.buttonColors(
                                    containerColor = surface,
                                    contentColor = onSurfaceVariant
                                )
                            },
                            border = BorderStroke(
                                width = 1.dp,
                                brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                            ),
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier
                                .height(56.dp)
                                .fillMaxWidth()
                        ) {
                            if (repeatDbState.startDate != null) {
                                Text(
                                    text = "${repeatDbState.startDateString}",
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(
                                    "Error"
                                )
                            }
                        }
                    }
                }

                item {
                    Column(

                    ) {
                        Text(text = "Ends")

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            RadioButton(
                                selected = repeatDbState.selectedRadioButton == 1,
                                onClick = { onRepeatDbEvent(RepeatDbEvent.SetRepeatEndType(1)) }
                            )

                            Text("Never")
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            RadioButton(
                                selected = repeatDbState.selectedRadioButton == 2,
                                onClick = { onRepeatDbEvent(RepeatDbEvent.SetRepeatEndType(2)) }
                            )

                            Text("On")

                            Button(
                                onClick = { onUiEvent(UiEvent.OpenDatePickDialog(2)) },
                                shape = MaterialTheme.shapes.small,
                                colors = MaterialTheme.colorScheme.run {
                                    ButtonDefaults.buttonColors(
                                        containerColor = surface,
                                        contentColor = onSurfaceVariant
                                    )
                                },
                                border = BorderStroke(
                                    width = 1.dp,
                                    brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                                ),
                                contentPadding = PaddingValues(8.dp),
                                modifier = Modifier
                                    .height(56.dp)
                                    .fillMaxWidth()
                            ) {
                                if (repeatDbState.endDate != null) {
                                    Text(
                                        text = "${repeatDbState.endDateString}",
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        "Error"
                                    )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            RadioButton(
                                selected = repeatDbState.selectedRadioButton == 3,
                                onClick = { onRepeatDbEvent(RepeatDbEvent.SetRepeatEndType(3)) }
                            )

                            Text("After")

                            BasicTextField(
                                value = repeatDbState.endAfterRepeats,
                                onValueChange = {
                                    if (it.length <= maxCharacter) onRepeatDbEvent(RepeatDbEvent.SetRepeatEnd(it))
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier,
                                decorationBox = { innerTextField ->
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .width(56.dp)
                                            .height(56.dp)
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.outlineVariant,
                                                shape = MaterialTheme.shapes.small
                                            ),
                                    ) {
                                        innerTextField()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}