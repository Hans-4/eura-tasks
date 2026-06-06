package me.hannes.eura_tasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.hannes.eura_tasks.db.lists.DeletedUserListEntity
import me.hannes.eura_tasks.db.lists.ListDbDao
import me.hannes.eura_tasks.db.lists.ListDbEvent
import me.hannes.eura_tasks.db.lists.ListDbState
import kotlin.time.Clock
import kotlin.time.Instant

class ListDbViewModel(private val dao: ListDbDao): ViewModel() {

    private val _state = MutableStateFlow(ListDbState())
    val state = _state

    fun onEvent(event: ListDbEvent) {
        when(event) {
            is ListDbEvent.DeleteListById -> {
                val currentDateTime: Instant = Clock.System.now()

                viewModelScope.launch {
                    val deletedList = DeletedUserListEntity(
                        deletedUuid = dao.getListUuidById(event.id),
                        deletionDate = currentDateTime
                    )
                    dao.upsertDeletedList(deletedList)
                    dao.deleteListById(event.id)
                }
            }

            is ListDbEvent.DeleteListByName -> {
                val currentDateTime: Instant = Clock.System.now()
                viewModelScope.launch {
                    val deletedList = DeletedUserListEntity(
                        deletedUuid = dao.getListUuidByName(event.name),
                        deletionDate = currentDateTime
                    )
                    dao.upsertDeletedList(deletedList)
                    dao.deleteListByName(event.name)
                }
            }
        }
    }
}