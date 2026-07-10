package com.eura.tasks.db.tasks

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import java.util.UUID


@Entity(
    tableName = "tasks",
    indices = [Index(value = ["uuid"], unique = true)]
)
data class TaskEntity(
    val title: String,
    val description: String, //TODO: Refactor to nullable
    val isFavorite: Boolean,
    val isCompleted: Boolean,
    val hasTags: Boolean,
    val repeatType: Int? = null, //0: Day, 1: Week, 2: Month, 3: Year
    val dueDateTime: Instant?,
    val taskList: String,
    val creationTime: Instant,
    val uuid: String = UUID.randomUUID().toString(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)