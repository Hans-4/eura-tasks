package me.hannes.eura_tasks.db.tasks

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import kotlin.time.Instant

@Entity(tableName = "tasks")
data class TodoEntity(
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val isCompleted: Boolean,
    val date: String, //TODO: Eventually make to one val and replace data type with LocalDateTime
    val time: String,
    val taskList: String,
    val creationTime: Instant,
    val uuid: String = UUID.randomUUID().toString(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
