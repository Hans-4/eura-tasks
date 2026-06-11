package me.hannes.eura_tasks.db.lists

import me.hannes.eura_tasks.db.tasks.TaskDbEvent

sealed interface ListDbEvent {
    object SaveList: ListDbEvent
    data class SetListTitle(val title: String): ListDbEvent
    data class SetListType(val type: String): ListDbEvent
    data class SetListColor(val color: String): ListDbEvent
    data class DeleteListByName(val name: String): ListDbEvent
}