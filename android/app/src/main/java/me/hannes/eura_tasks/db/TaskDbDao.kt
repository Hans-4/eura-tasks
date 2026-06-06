package me.hannes.eura_tasks.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.hannes.eura_tasks.db.deletedTasks.DeletedTasksEntity
import me.hannes.eura_tasks.db.tasks.TodoEntity

@Dao
interface TaskDbDao {
    @Upsert
    suspend fun upsertTask(todo: TodoEntity)
    @Upsert
    suspend fun upsertDeletedTask(deletedTask: DeletedTasksEntity)
    @Update
    suspend fun update(todo: TodoEntity)
    @Delete
    suspend fun deleteTodo(todo: TodoEntity)
    @Query("SELECT uuid FROM tasks WHERE id = :id")
    suspend fun getTaskUuid(id: Int): String
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTodoById(id: Int)
    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getAllTasksByIdAsc(): Flow<List<TodoEntity>>
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasksByIdDesc(): Flow<List<TodoEntity>>
    @Query("SELECT * FROM tasks ORDER BY title ASC")
    fun getAllTodosByTitleAsc(): Flow<List<TodoEntity>>
    @Query("SELECT * FROM tasks ORDER BY date ASC")
    fun getAllTasksByDateAsc(): Flow<List<TodoEntity>>
    @Query("SELECT EXISTS(SELECT 1 FROM tasks WHERE uuid = :uuid)")
    suspend fun exists(uuid: String): Boolean
}