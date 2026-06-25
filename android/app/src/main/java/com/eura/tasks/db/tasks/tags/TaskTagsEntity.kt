package com.eura.tasks.db.tasks.tags

import androidx.room.Entity
import androidx.room.ForeignKey
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.TaskEntity

@Entity(
    tableName = "task_tags",
    primaryKeys = ["taskUuid", "tagUuid"],
    foreignKeys = [
        ForeignKey(
            entity = TagsEntity::class,
            parentColumns = ["uuid"],
            childColumns = ["tagUuid"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["uuid"],
            childColumns = ["taskUuid"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskTagsEntity(
    val taskUuid: String,
    val tagUuid: String,
)