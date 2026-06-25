package com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.addList.addListComponents

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eura.tasks.R
import com.eura.tasks.ui.UiEvent

@Composable
fun ItemWithSimilarNameWarningDialog(
    onUiEvent: (UiEvent) -> Unit,
    reason: Int
) {
    val type = when (reason) {
        1 -> stringResource(R.string.list)
        2 -> stringResource(R.string.tag)
        else -> ":("
    }

    AlertDialog(
        title =  {Text("$type can not be created")},
        text = {
            Text("There is already a $type with a similar name. Chose a different name.")
        },
        onDismissRequest = {onUiEvent(UiEvent.CloseItemWithSimilarNameWarningDialog)},
        confirmButton = {
            TextButton(
                onClick = {
                    onUiEvent(UiEvent.CloseItemWithSimilarNameWarningDialog)
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
    )
}