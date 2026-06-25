package com.eura.tasks.db.lists

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "user_lists")
data class UserListEntity(
    override val title: String,
    override val type: String,
    override val colorString: String,
    val uuid: String = UUID.randomUUID().toString(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
) : TaskList