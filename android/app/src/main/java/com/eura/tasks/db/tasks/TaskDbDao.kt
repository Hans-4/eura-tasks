package com.eura.tasks.db.tasks

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.eura.tasks.db.deletedItems.DeletedItemsEntity
import com.eura.tasks.db.tasks.tags.TaskTagsEntity
import kotlinx.coroutines.flow.Flow
import com.eura.tasks.db.tasks.tags.TaskWithTags
import kotlinx.datetime.Instant

@Dao
interface TaskDbDao {
    @Upsert
    suspend fun upsertTask(todo: TaskEntity): String

    @Update
    suspend fun updateTask(task: TaskEntity)
    @Delete
    suspend fun deleteTask(todo: TaskEntity)
    @Query("DELETE FROM tasks WHERE taskUuid = :uuid")
    suspend fun deleteTaskByUuid(uuid: String)
    @Query("DELETE FROM tasks WHERE parentListId = :listId")
    suspend fun deleteTasksByListId(listId: String)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM deleted_items WHERE type = 1")
    suspend fun getAllDeletedTasks(): List<DeletedItemsEntity>
    @Query("SELECT * FROM tasks WHERE taskUuid = :id")
    suspend fun getTasksById(id: List<String>): List<TaskEntity>
    @Query("SELECT * FROM tasks WHERE taskUuid = :id")
    suspend fun getTaskById(id: String): TaskEntity?
    @Query("DELETE FROM tasks WHERE taskUuid = :uuid")
    suspend fun deleteTodoByUuid(uuid: String)
    @Query("SELECT * FROM tasks ORDER BY taskUuid ASC")
    fun getAllTasksByUuidAsc(): Flow<List<TaskEntity>>
    @Query("SELECT * FROM tasks ORDER BY taskUuid DESC")
    fun getAllTasksByUuidDesc(): Flow<List<TaskEntity>>
    @Query("SELECT * FROM tasks ORDER BY title ASC")
    fun getAllTodosByTitleAsc(): Flow<List<TaskEntity>>
    @Query("SELECT * FROM tasks ORDER BY notificationTime ASC")
    fun getAllTasksByDateAsc(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE LOWER(title) LIKE LOWER('%' || :query || '%')")
    suspend fun searchForTasks(query: String): List<TaskEntity>

    @Query("UPDATE tasks SET title = :newTitle WHERE taskUuid = :uuid")
    suspend fun updateTaskTitle(uuid: String, newTitle: String)
    @Query("UPDATE tasks SET description = :newDescription WHERE taskUuid = :uuid")
    suspend fun updateTaskDescription(uuid: String, newDescription: String)
    @Query("UPDATE tasks SET isCompleted = 1 WHERE taskUuid = :uuid")
    suspend fun markAsCompleteByUuid(uuid: String)
    @Query("UPDATE tasks SET notificationTime = :newDateTime WHERE taskUuid = :uuid")
    suspend fun updateTaskDateTime(uuid: String, newDateTime: Instant?)

    @Transaction
    @Query("SELECT * FROM tasks WHERE parentListId = :taskList")
    suspend fun getTaskWithTags(taskList: String): TaskWithTags?
    @Query("SELECT * FROM tasks WHERE taskUuid IN (:uuids)")
    suspend fun getTasksByUuids(uuids: List<String>): List<TaskEntity>
    @Query("SELECT * FROM task_tags WHERE tagUuid = :tagUuid")
    suspend fun getAllTasksFromTagByUuid(tagUuid: String): List<TaskTagsEntity>


    @Query("SELECT notificationTime FROM tasks WHERE taskUuid = :uuid")
    suspend fun getNotificationTimeByUuid(uuid: String): Instant?
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND notificationTime > :now")
    suspend fun getAllActiveTasksWithAlarms(now: Instant): List<TaskEntity>

    @Query("SELECT listId FROM user_lists WHERE title = :title")
    suspend fun getParentListId(title: String): String?

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 AND parentListId = :listId")
    suspend fun getAllCompletedTasksFromList(listId: String): List<TaskEntity>
}
