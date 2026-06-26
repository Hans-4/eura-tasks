package com.eura.tasks.db.tasks

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Instant

@Entity(
    tableName = "tasks",
    indices = [Index(value = ["uuid"], unique = true)]
)
data class TaskEntity(
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val isCompleted: Boolean,
    val hasTags: Boolean,
    val dueDateTime: LocalDateTime?,
    val taskList: String,
    val creationTime: Instant,
    val uuid: String = UUID.randomUUID().toString(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)