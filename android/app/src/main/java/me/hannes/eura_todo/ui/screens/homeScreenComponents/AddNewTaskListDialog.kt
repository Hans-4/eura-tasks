package me.hannes.eura_todo.ui.screens.homeScreenComponents

import android.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.hannes.eura_todo.R
import me.hannes.eura_todo.ui.UiEvent

@Composable
fun AddNewTaskListDialog(
    onUiEvent: (UiEvent) -> Unit,
) {
    AlertDialog(
        title =  {Text("Add new list")},
        onDismissRequest = {onUiEvent(UiEvent.CloseAddTaskListDialog)},
        confirmButton = {
            TextButton(
                onClick = {TODO()}
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
        text = {
            Column(

            ) {
                TextField(
                    value = "",
                    onValueChange = {TODO()}
                )
            }
        }
    )
}