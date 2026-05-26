package me.hannes.eura_todo.ui.viewModels

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dataStore = application.dataStore

    companion object {
        val INITIAL_LIST = listOf("My Tasks")
    }

    val itemList: Flow<List<String>> = dataStore.data.map { prefs ->
        prefs[SettingsKeys.TASK_LISTS]?.toList() ?: INITIAL_LIST
    }

    val selectedListIndex: Flow<Int> = dataStore.data.map { prefs ->
        prefs[SettingsKeys.SELECTED_LIST_INDEX] ?: 0
    }

    fun setSelectedListIndex(index: Int) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[SettingsKeys.SELECTED_LIST_INDEX] = index
            }
        }
    }

    fun addItem(newItem: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                val currentSet = prefs[SettingsKeys.TASK_LISTS] ?: INITIAL_LIST.toSet()

                prefs[SettingsKeys.TASK_LISTS] = currentSet + newItem
            }
        }
    }

    fun removeItem(item: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                val currentSet = prefs[SettingsKeys.TASK_LISTS] ?: INITIAL_LIST.toSet()

                prefs[SettingsKeys.TASK_LISTS] = currentSet - item
            }
        }
    }
}