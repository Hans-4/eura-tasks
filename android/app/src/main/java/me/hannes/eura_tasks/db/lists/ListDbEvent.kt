package me.hannes.eura_tasks.db.lists

sealed interface ListDbEvent {
    data class DeleteListById(val id: Int): ListDbEvent
    data class DeleteListByName(val name: String): ListDbEvent
}