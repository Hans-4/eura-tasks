package com.eura.tasks.db.lists

import com.eura.tasks.db.deletedItems.DeletedItemsEntity

data class ListDbState(
    val userLists: List<UserListEntity> = emptyList(),
    val deletedUserList: List<DeletedItemsEntity> = emptyList(),
    val listTitle: String = "",
    val listType: String = "OTHER",
    val listColor: String = "RED",

    val searchQuery: String = "",
    val searchResults: List<UserListEntity> = emptyList()
)
