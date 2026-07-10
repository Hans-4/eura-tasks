package com.eura.tasks.db.tags

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "tags",
)
data class TagsEntity(
    val title: String,
    @PrimaryKey
    val tagUuid: String = UUID.randomUUID().toString(),
)