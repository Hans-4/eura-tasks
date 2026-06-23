package com.eura.tasks.db.tasks.tags

import androidx.room.Entity
import androidx.room.ForeignKey
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.TaskEntity

@Entity(
    tableName = "task_tags",
    primaryKeys = ["taskId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = TagsEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskTagsEntity(
    val taskId: Int,
    val tagId: Int,
)