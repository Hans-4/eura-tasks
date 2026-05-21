package me.hannes.eura_todo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Upsert
    suspend fun upsertTask(todo: TodoEntity)
    @Update
    suspend fun update(todo: TodoEntity)
    @Delete
    suspend fun deleteTodo(todo: TodoEntity)
    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getAllTasksByIdAsc(): Flow<List<TodoEntity>>
    @Query("SELECT * FROM tasks ORDER BY title ASC")
    fun getAllTodosByTitleAsc(): Flow<List<TodoEntity>>
    @Query("SELECT * FROM tasks ORDER BY date ASC")
    fun getAllTasksByDateAsc(): Flow<List<TodoEntity>>
}