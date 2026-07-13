package com.eura.tasks.db.tags

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

@Entity(
    tableName = "tags",
)
data class TagsEntity(
    val title: String,
    val creationTime: Instant = Clock.System.now(),
    val updateTime: Instant = Clock.System.now(),
    @PrimaryKey
    val tagUuid: String = UUID.randomUUID().toString(),
)