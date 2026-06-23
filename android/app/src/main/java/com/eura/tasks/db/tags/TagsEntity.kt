package com.eura.tasks.db.tags

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tags")
data class TagsEntity(
    val name: String,
    val uuid: String = UUID.randomUUID().toString(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)