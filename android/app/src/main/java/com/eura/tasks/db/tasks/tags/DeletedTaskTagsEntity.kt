package com.eura.tasks.db.tasks.tags

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity(tableName = "deleted_task_tags")
data class DeletedTaskTagsEntity(
    val deletedTaskUuid: String,
    val deletedTagUuid: String,
    val deletionDate: Instant,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
