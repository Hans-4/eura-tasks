package com.eura.tasks.db.lists

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import java.util.UUID
import com.eura.tasks.ui.Converter
import com.eura.tasks.cloudSync.GoogleDriveViewModel
import kotlinx.datetime.Clock

/**
 * Represents a single task item within the application database.
 * @property listId The unique identifier for the list, generated locally as a UUID string.
 * @property title The main headline or name of the list.
 * @property type The type of the list which is used from [Converter] to get the correct icon.
 * @property colorString The color of the list which is used from [Converter] to get the correct color.
 * @property creationTime Timestamp indicating when the list was originally created which is required from [GoogleDriveViewModel]
 * @property updateTime Timestamp indicating when the list was last modified which is required from [GoogleDriveViewModel]
 */
@Entity(tableName = "user_lists")
data class UserListEntity(
    override val title: String,
    override val type: String,
    override val colorString: String,

    val creationTime: Instant = Clock.System.now(),
    val updateTime: Instant = Clock.System.now(),

    @PrimaryKey
    override val listId: String = UUID.randomUUID().toString(),
): TaskListInterface