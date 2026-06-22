package me.hannes.eura_tasks.ui.screens.homeScreenComponents.addTaskBottomSheetComponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.hannes.eura_tasks.R

@Composable
fun AddTagsDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val testList = listOf(
        "Important",
        "Shopping",
        "f",
        "jdf",
        "fdfsf",
        "fdldf",
        "jfldf",
        "jldf"
    )

    var checked by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = { onConfirm() }
            ) {
                Text(
                    text = stringResource(R.string.save)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text(
                    text = stringResource(R.string.cancel)
                )
            }
        },
        title = {
            Text(
                text = "Add tags"
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Card(
                    modifier = Modifier
                        .heightIn(max = 280.dp)
                        .fillMaxWidth(),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                    ),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        itemsIndexed(testList) { index, item ->
                            Button(
                                onClick = { checked = !checked },
                                shape = RoundedCornerShape(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Checkbox(
                                        checked = checked,
                                        onCheckedChange = { checked = it }
                                    )

                                    Text(text = item)
                                }
                            }

                            HorizontalDivider()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        width = 1.dp,
                        brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "plus"
                        )
                        Text(
                            text = "Add new Tag"
                        )
                    }
                }
            }
        }
    )
}

@Preview(
    showBackground = true
)
@Composable
fun p () {
    AddTagsDialog(
        onDismiss = {},
        onConfirm = {}
    )
}