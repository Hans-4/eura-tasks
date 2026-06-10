package me.hannes.eura_tasks.db

import me.hannes.eura_tasks.db.lists.DeletedUserListEntity
import me.hannes.eura_tasks.db.lists.UserListEntity
import me.hannes.eura_tasks.db.tasks.SortType
import me.hannes.eura_tasks.db.tasks.TaskEntity

data class DbState(
    val userLists: List<UserListEntity> = emptyList(),
    val deletedUserList: List<DeletedUserListEntity> = emptyList(),
    val listTitle: String = "",
    val listType: String = "OTHER",
    val listColor: String = "RED",

    val tasks: List<TaskEntity> = emptyList(),
    val todoTitle: String = "",
    val todoDescription: String = "",
    val todoIsFavorite: Boolean = false,
    val todoIsCompleted: Boolean = false,
    val todoDate: String = "",
    val todoTime: String = "",
    val taskParentList: String = "",
    val sortType: SortType = SortType.TITLE,

    val searchQuery: String = "",
)