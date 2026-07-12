package com.eura.tasks.ui.screens.taskScreen.taskScreenSubScreens.taskDetailsScreen.taskDetailsSubScreen.tagManagmentScreen.components

import androidx.compose.foundation.layout.Arrangement
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
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.db.tasks.tags.TaskTagsEntity

@Composable
fun TagItem(
    onTagList: (String) -> Unit,

    tag: TagsEntity,
    taskTag: List<TaskTagsEntity>,
    task: TaskEntity,
    onTagDbEvent: (TagDbEvent) -> Unit
) {
    val checked = taskTag.any { it.tagUuid == tag.tagUuid && it.isActive }

    Button(
        onClick = { onTagList(tag.tagUuid) },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ),
    shape = RoundedCornerShape(16.dp),
    contentPadding = PaddingValues(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                onCheckedChange = {
                    onTagDbEvent(TagDbEvent.UpdateTaskTag(task.taskUuid, tag.tagUuid, it))
                },
                checked = checked
            )

            Text(
                text = tag.title,
                fontSize = 16.sp
            )
        }
    }
}