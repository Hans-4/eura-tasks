package me.hannes.eura_tasks.db.tasks

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDbDao {
    @Upsert
    suspend fun upsertTask(todo: TaskEntity)
    @Upsert
    suspend fun upsertDeletedTask(deletedTask: DeletedTasksEntity)
    @Update
    suspend fun update(todo: TaskEntity)
    @Delete
    suspend fun deleteTodo(todo: TaskEntity)
    @Query("SELECT * FROM deleted_tasks")
    suspend fun getAllDeletedTasks(): List<DeletedTasksEntity>
    @Query("DELETE FROM deleted_tasks WHERE deletionDate < :cutoffTimestamp")
    suspend fun deleteLogsOlderThan(cutoffTimestamp: Long)
    @Query("SELECT uuid FROM tasks WHERE id = :id")
    suspend fun getTaskUuid(id: Int): String
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTodoById(id: Int)
    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getAllTasksByIdAsc(): Flow<List<TaskEntity>>
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasksByIdDesc(): Flow<List<TaskEntity>>
    @Query("SELECT * FROM tasks ORDER BY title ASC")
    fun getAllTodosByTitleAsc(): Flow<List<TaskEntity>>
    @Query("SELECT * FROM tasks ORDER BY dueDateTime ASC")
    fun getAllTasksByDateAsc(): Flow<List<TaskEntity>>
    @Query("SELECT EXISTS(SELECT 1 FROM tasks WHERE uuid = :uuid)")
    suspend fun taskExists(uuid: String): Boolean
    @Query("SELECT EXISTS(SELECT 1 FROM deleted_tasks WHERE deletedUuid = :uuid)")
    suspend fun deleted(uuid: String): Boolean
    @Query("DELETE FROM tasks WHERE taskList = :listName")
    suspend fun deleteTasksByListName(listName: String)
    @Query("SELECT * FROM tasks WHERE LOWER(title) LIKE LOWER('%' || :query || '%')")
    suspend fun searchForTasks(query: String): List<TaskEntity>
}