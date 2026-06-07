package me.hannes.eura_tasks.ui.screens.homeScreenComponents.addNewTaskListComponents

import android.app.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.hannes.eura_tasks.R
import me.hannes.eura_tasks.db.lists.ListDbEvent
import me.hannes.eura_tasks.ui.UiEvent
import me.hannes.eura_tasks.ui.UiState

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