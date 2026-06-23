package com.eura.tasks.ui.screens.homeScreenComponents.settingsChildrenScreens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import com.eura.tasks.R
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.eura.tasks.ui.SYNC_INTERVAL_OPTIONS

@Composable
fun ManageSyncIntervalsDialog(
    onConfirm: (Int) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = { onConfirm(selectedIndex) },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedIndex) }
            ) {
                Text(
                    text = stringResource(R.string.confirm)
                )
            }
        },
        title = {
            Text(
                text = "Manage sync intervals"
            )
        },
        text = {
            LazyColumn(

            ) {
                itemsIndexed(SYNC_INTERVAL_OPTIONS) { index, option ->
                    Button(
                        onClick = { selectedIndex = index },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = index == selectedIndex,
                                onClick = { selectedIndex = index }
                            )
                            Text(
                                text = option
                            )
                        }

                    }
                }
            }
        }
    )
}