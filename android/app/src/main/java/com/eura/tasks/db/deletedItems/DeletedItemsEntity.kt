package com.eura.tasks.db.deletedItems

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Contains all deleted items which is used for the cloud sync.
 * @property deletedUuid The previews identifier for the item.
 * @property deletionTime The time when the item was deleted.
 * @property type The type of the item `1` (task), `2` (list) `3` (tag)
 */
@Entity(tableName = "deleted_items")
data class DeletedItemsEntity(
    val deletionTime: Instant = Clock.System.now(),
    val type: Int,
    @PrimaryKey
    val deletedUuid: String,
)
