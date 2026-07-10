package com.eura.tasks.ui.screens.searchScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.TaskEntity

@Composable
fun TaskItem(
    item: TaskEntity,
    onTaskDetails: (String, String) -> Unit,
    parentScreen: String,
    onTaskDbEvent: (TaskDbEvent) -> Unit,
) {
    Button(
        onClick = { onTaskDetails(item.taskUuid, parentScreen) },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                onClick = {
                    onTaskDbEvent(TaskDbEvent.SetIsCompleted(!item.isCompleted, item))
                },
                selected = item.isCompleted
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, fontSize = 18.sp)
                Text(
                    text = item.parentListId,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = {
                    onTaskDbEvent(TaskDbEvent.SetTodoIsFavorite(!item.isFavorite, item))
                }
            ) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = "Toggle favorite",
                )
            }
        }
    }
}