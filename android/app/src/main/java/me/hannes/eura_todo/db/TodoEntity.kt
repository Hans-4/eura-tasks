package me.hannes.eura_todo.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.sql.Date

@Entity(tableName = "tasks")
data class TodoEntity(
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val isCompleted: Boolean,
    val date: String,
    val time: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
