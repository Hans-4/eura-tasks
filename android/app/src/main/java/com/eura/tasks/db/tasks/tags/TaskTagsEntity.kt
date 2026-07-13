package com.eura.tasks.db.tasks.tags

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.TaskEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "task_tags",
    foreignKeys = [
        ForeignKey(
            entity = TagsEntity::class,
            parentColumns = ["tagUuid"],
            childColumns = ["tagUuid"],
            onDelete = ForeignKey.CASCADE
        ),

        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["taskUuid"],
            childColumns = ["taskUuid"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class TaskTagsEntity(
    val taskUuid: String,
    val tagUuid: String,
    val isActive: Boolean = false,
    val updateTime: Instant = Clock.System.now(),
    @PrimaryKey
    val uuid: String = "$taskUuid$tagUuid"
)