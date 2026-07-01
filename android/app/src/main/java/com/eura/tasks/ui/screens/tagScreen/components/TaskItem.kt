package com.eura.tasks.ui.screens.tagScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.db.tasks.tags.TaskTagsEntity

@Composable
fun TaskItem(
    item: TaskEntity,
    onTaskDetails: (Int, String) -> Unit,
    parentScreen: String,
    onTaskDbEvent: (TaskDbEvent) -> Unit,

    tagEntity: TagsEntity,
    taskTags: List<TaskTagsEntity>
) {
    val checked = taskTags.any { it.taskId == item.id }

    Button(
        onClick = { onTaskDetails(item.id, parentScreen) },
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
            Checkbox(
                onCheckedChange = {
                    if (checked) {
                        onTaskDbEvent(TaskDbEvent.RemoveFromTagByTaskId(item.id))
                    } else {
                        onTaskDbEvent(
                            TaskDbEvent.InsertNewTaskTag(
                                item.id,
                                item.uuid,
                                tagEntity.id,
                                tagEntity.uuid
                            )
                        )
                    }
                },
                checked = checked
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, fontSize = 18.sp)
                Text(
                    text = item.taskList,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}