package com.eura.tasks.db.lists

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "deleted_user_lists")
data class DeletedUserListEntity(
    val deletedUuid: String,
    val deletionDate: Instant,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)