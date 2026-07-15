package com.eura.tasks.db.lists

sealed interface ListDbEvent {
    object SaveList: ListDbEvent
    data class SetListTitle(val title: String): ListDbEvent
    data class SetListType(val type: String): ListDbEvent
    data class SetListColor(val color: String): ListDbEvent
    data class DeleteListById(val id: String): ListDbEvent

    data class SetUpdateListTitle(val title: String): ListDbEvent
    data class RenameList(val listId: String): ListDbEvent

    data class GetListsById(val listId: String): ListDbEvent
}