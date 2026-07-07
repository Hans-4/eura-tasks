package com.eura.tasks.db.tasks

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.eura.tasks.db.tasks.tags.TaskTagsEntity
import kotlinx.coroutines.flow.Flow
import com.eura.tasks.db.tasks.tags.TaskWithTags
import kotlinx.datetime.Instant

@Dao
interface TaskDbDao {
    @Upsert
    suspend fun upsertTask(todo: TaskEntity): Long
    @Upsert
    suspend fun upsertDeletedTask(deletedTask: DeletedTasksEntity)
    @Update
    suspend fun update(todo: TaskEntity)
    @Delete
    suspend fun deleteTask(todo: TaskEntity)
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)
    @Query("DELETE FROM tasks WHERE taskList = :listName")
    suspend fun deleteTasksByListName(listName: String)
    @Query("DELETE FROM deleted_tasks WHERE deletionDate < :cutoffTimestamp")
    suspend fun deleteLogsOlderThan(cutoffTimestamp: Long)

    @Query("SELECT * FROM deleted_tasks")
    suspend fun getAllDeletedTasks(): List<DeletedTasksEntity>
    @Query("SELECT uuid FROM tasks WHERE id = :id")
    suspend fun getTaskUuid(id: Int): String
    @Query("SELECT id FROM tasks WHERE uuid = :uuid")
    suspend fun getTaskIdByUuid(uuid: String): Int
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
    @Query("SELECT * FROM tasks WHERE LOWER(title) LIKE LOWER('%' || :query || '%')")
    suspend fun searchForTasks(query: String): List<TaskEntity>

    @Query("UPDATE tasks SET title = :newTitle WHERE id = :id")
    suspend fun updateTaskTitle(id: Int, newTitle: String)
    @Query("UPDATE tasks SET description = :newDescription WHERE id = :id")
    suspend fun updateTaskDescription(id: Int, newDescription: String)
    @Query("UPDATE tasks SET isCompleted = 1 WHERE id = :id")
    suspend fun markAsCompleteById(id: Int)
    @Query("UPDATE tasks SET dueDateTime = :newDateTime WHERE id = :id")
    suspend fun updateTaskDateTime(id: Int, newDateTime: Instant)
    
    @Transaction
    @Query("SELECT * FROM tasks WHERE taskList = :taskList")
    suspend fun getTaskWithTags(taskList: String): TaskWithTags?
    @Query("SELECT * FROM tasks WHERE id IN (:ids)")
    suspend fun getTasksByIds(ids: List<Int>): List<TaskEntity>
    @Query("SELECT * FROM task_tags WHERE tagId = :tagId")
    suspend fun getAllTasksFromTagById(tagId: Int): List<TaskTagsEntity>

    @Query("DELETE FROM task_tags WHERE taskId = :taskId")
    suspend fun removeTagByTaskId(taskId: Int)


    @Query("SELECT * FROM tasks WHERE fullDay = 1 AND isCompleted = 0")
    suspend fun getAllFullDayTasks(): List<TaskEntity>

    @Query("SELECT dueDateTime FROM tasks WHERE id = :id")
    suspend fun getDueDateTimeById(id: Int): Instant?
}