package com.eura.tasks.db.tasks

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.eura.tasks.db.lists.UserListEntity
import kotlinx.datetime.Instant
import java.util.UUID
import com.eura.tasks.db.Converters

/**
 * Represents a single task item within the application database.
 *
 * @property taskUuid The unique identifier for the task, generated locally as a UUID string.
 * @property parentListId The ID of the [UserListEntity] this task belongs to.
 * @property title The main headline or name of the task.
 * @property description Optional detailed notes or description for the task.
 * @property isFavorite True if the task has been flagged/starred by the user.
 * @property isCompleted True if the task is marked as finished.
 * @property hasTags True if the task has any associated tags (used for quick filtering optimizations).
 * @property repeatType The recurrence interval: `0` (Day), `1` (Week), `2` (Month), `3` (Year), or `null` if it doesn't repeat.
 * @property notificationTime The scheduled reminder time, converted to/from ISO-8601 via [Converters]. Null if no reminder is set.
 * @property creationTime Timestamp indicating when the task was originally created.
 * @property updateTime Timestamp indicating when the task was last modified.
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = UserListEntity::class,
            parentColumns = ["listId"],
            childColumns = ["parentListId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskEntity(
    override val title: String,
    override val description: String? = null,
    override val isFavorite: Boolean,
    override val isCompleted: Boolean,
    override val hasTags: Boolean,
    override val repeatType: Int? = null,
    override val notificationTime: Instant?,

    override val parentListId: String,

    override val creationTime: Instant,
    override val updateTime: Instant,

    @PrimaryKey
    override val taskUuid: String = UUID.randomUUID().toString(),
): TaskInterface