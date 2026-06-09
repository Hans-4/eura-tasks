package me.hannes.eura_tasks.db.lists

data class ListDbState(
    val userLists: List<UserListEntity> = emptyList(),
    val deletedUserList: List<DeletedUserListEntity> = emptyList(),
    val listTitle: String = "",
    val listType: String = "OTHER",
    val listColor: String = "RED"
)
