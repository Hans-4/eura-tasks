package me.hannes.eura_tasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.hannes.eura_tasks.db.cleanUpOldLists
import me.hannes.eura_tasks.db.lists.DeletedUserListEntity
import me.hannes.eura_tasks.db.lists.ListDbDao
import me.hannes.eura_tasks.db.lists.ListDbEvent
import me.hannes.eura_tasks.db.lists.ListDbState
import me.hannes.eura_tasks.db.lists.UserListEntity
import me.hannes.eura_tasks.ui.UiEvent
import kotlin.time.Clock
import kotlin.time.Instant

class ListDbViewModel(private val dao: ListDbDao): ViewModel() {

    private val _state = MutableStateFlow(ListDbState())
    val state = combine(_state, dao.getAllLists()) { state, userLists ->
        state.copy(
            userLists = userLists
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListDbState())

    fun onEvent(event: ListDbEvent, onUiEvent: (UiEvent) -> Unit) {
        when(event) {
            ListDbEvent.SaveList ->  {
                val title = _state.value.listTitle.trim()
                val type = _state.value.listType
                val color = _state.value.listColor

                if (title.isBlank() || type.isBlank() || color.isBlank()) {
                    return
                }

                viewModelScope.launch {
                    if (dao.searchForExistingTitle(title)) {
                        onUiEvent(UiEvent.OpenListWithSimilarNameWarningDialog)
                    } else {
                        val list = UserListEntity(
                            name = title,
                            type = type,
                            colorString = color
                        )
                        dao.upsertList(list)
                        _state.update { 
                            it.copy(
                                listTitle = "",
                                listType = "OTHER",
                                listColor = "RED"
                            ) 
                        }
                        cleanUpOldLists(dao)
                        onUiEvent(UiEvent.CloseAddTaskListDialog)
                    }
                }
            }
            is ListDbEvent.SetListColor -> {
                _state.update {
                    it.copy(
                        listColor = event.color
                    )
                }
            }
            is ListDbEvent.SetListTitle -> {
                _state.update {
                    it.copy(
                        listTitle = event.title
                    )
                }
            }
            is ListDbEvent.SetListType -> {
                _state.update {
                    it.copy(
                        listType = event.type
                    )
                }
            }
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