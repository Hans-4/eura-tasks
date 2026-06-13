package me.hannes.eura_tasks.ui.screens.sychronice

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.hannes.eura_tasks.R
import me.hannes.eura_tasks.db.lists.UserListEntity
import me.hannes.eura_tasks.ui.Converter

@Composable
fun ListConflictWarningDialog(
    localList: UserListEntity,
    remoteList: UserListEntity,
    onResolve: (UserListEntity) -> Unit
) {
    val items = listOf(localList, remoteList)
    var selectedIndex by remember { mutableIntStateOf(0) }

    BackHandler(
        enabled = false,
        onBack = {}
    )

    AlertDialog(
        onDismissRequest = { /* Prevent dismissal without choice? Or choose local by default? */ },
        confirmButton = {
            TextButton(onClick = { onResolve(items[selectedIndex]) }) {
                Text(stringResource(R.string.confirm))
            }
        },
        title = { Text("List Conflict Detected") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("A list with the name \"${localList.name}\" already exists locally but has different settings. Which one do you want to keep? Your tasks in the list will not be affected.")

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    border = BorderStroke(
                        width = 1.dp,
                        brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        items.forEachIndexed { index, item ->
                            val isLocal = index == 0
                            val icon = Converter.typeIconConverter(item.type)
                            val color = Converter.colorStringConverter(
                                systemThemeIndex = 0,
                                colorString = item.colorString
                            )

                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { selectedIndex = index },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(0.dp),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    RadioButton(
                                        selected = selectedIndex == index,
                                        onClick = { selectedIndex = index }
                                    )
                                    Surface(
                                        color = color.primaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = color.onPrimaryContainer,
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = if (isLocal) "Local Version" else "Cloud Version",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "${item.type} (${item.colorString})",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }

                            if (index == 0) {
                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
