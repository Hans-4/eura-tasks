package com.eura.tasks.db.tags

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "tags",
    indices = [Index(value = ["uuid"], unique = true)]
)
data class TagsEntity(
    val title: String,
    val uuid: String = UUID.randomUUID().toString(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)