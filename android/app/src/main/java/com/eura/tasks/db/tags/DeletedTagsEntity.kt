package com.eura.tasks.db.tags

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity(tableName = "deleted_tags")
data class DeletedTagsEntity(
    val deletedUuid: String,
    val deletionDate: Instant,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)