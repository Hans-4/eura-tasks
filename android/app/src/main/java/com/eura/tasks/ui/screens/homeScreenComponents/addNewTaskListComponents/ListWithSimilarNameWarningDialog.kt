package com.eura.tasks.ui.screens.homeScreenComponents.addNewTaskListComponents

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eura.tasks.R
import com.eura.tasks.ui.UiEvent

@Composable
fun ListWithSimilarNameWarningDialog(
    onUiEvent: (UiEvent) -> Unit
) {
    AlertDialog(
        title =  {Text("List can not be created")},
        text = {
            Text("There is already a list with a similar name. Chose a different name.")
        },
        onDismissRequest = {onUiEvent(UiEvent.CloseListWithSimilarNameWarningDialog)},
        confirmButton = {
            TextButton(
                onClick = {
                    onUiEvent(UiEvent.CloseListWithSimilarNameWarningDialog)
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
    )
}