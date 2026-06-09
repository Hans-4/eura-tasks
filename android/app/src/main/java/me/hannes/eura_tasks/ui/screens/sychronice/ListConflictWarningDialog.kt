package me.hannes.eura_tasks.ui.screens.sychronice

import android.util.Log
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
import me.hannes.eura_tasks.ui.Converter

data class TestItems(
    val name: String,
    val type: String,
    val color: String,
    val selected: Boolean
)
@Composable
fun ListConflictWarningDialog(
    onClose: () -> Unit
) {
    val items = listOf(
        TestItems(
            name = "1",
            type = "TRAVEL",
            color = "BLUE",
            selected = true
        ),
        TestItems(
            name = "1",
            type = "REMINDERS",
            color = "GREEN",
            selected = false
        )
    )

    var selectedIndex by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = { onClose() },
        confirmButton = {
            TextButton(onClick = { onClose() }) {
                Text(stringResource(R.string.confirm))
            }
        },
        title = { Text("There is a conflict") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("You got a list which you already have locally but with a different color or type. Which one do you want to use?")

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    border = BorderStroke(
                        width = 1.dp,
                        brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        val listSize = items.size
                        items.forEachIndexed { index, item ->
                            Log.d("Type", "Type string is ${item.type}")
                            val icon = Converter.typeIconConverter(item.type)
                            val color = Converter.colorStringConverter(
                                systemThemeIndex = 0,
                                colorString = item.color
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
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(item.name)
                                }
                            }

                            if (index != listSize - 1) {
                                HorizontalDivider(
                                    thickness = 2.dp,
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