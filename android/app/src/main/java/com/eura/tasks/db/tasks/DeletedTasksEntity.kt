package com.eura.tasks.db.tasks

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "deleted_tasks")
data class DeletedTasksEntity(
    val deletedUuid: String,
    val deletionDate: Instant,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)