package me.hannes.eura_tasks.db.lists

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "user_lists")
data class UserListEntity(
    val name: String,
    val type: String,
    val colorString: String,
    val uuid: String = UUID.randomUUID().toString(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)