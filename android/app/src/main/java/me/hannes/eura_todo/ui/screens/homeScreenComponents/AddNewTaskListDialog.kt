package me.hannes.eura_todo.ui.screens.homeScreenComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import me.hannes.eura_todo.R
import me.hannes.eura_todo.ui.UiEvent
import me.hannes.eura_todo.ui.viewModels.SettingsViewModel

@Composable
fun AddNewTaskListDialog(
    onUiEvent: (UiEvent) -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    var newListName by remember { mutableStateOf("") }

    AlertDialog(
        title =  {Text("Add new list")},
        text = {
            Column(

            ) {
                TextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    placeholder = {
                        Text(
                            stringResource(R.string.enter_list_name)
                        )
                    },
                    singleLine = true
                )
            }
        },
        onDismissRequest = {onUiEvent(UiEvent.CloseAddTaskListDialog)},
        confirmButton = {
            TextButton(
                onClick = {
                    settingsViewModel.addItem(newListName)
                    onUiEvent(UiEvent.CloseAddTaskListDialog)
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {onUiEvent(UiEvent.CloseAddTaskListDialog)}
            ) {
                Text(
                    stringResource(R.string.cancel)
                )
            }
        },
    )
}