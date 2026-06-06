package me.hannes.eura_tasks.db.lists

data class ListDbState(
    val userLists: List<UserListEntity> = emptyList(),
    val listTitle: String = "",
)
