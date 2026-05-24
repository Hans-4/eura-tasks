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
        val DEFAULT_CATEGORIES = listOf("My Tasks")
    }

    val itemList: Flow<List<String>> = dataStore.data.map { prefs ->
        prefs[SettingsKeys.TASK_LISTS]?.toList() ?: DEFAULT_CATEGORIES
    }

    fun addItem(newItem: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                val currentSet = prefs[SettingsKeys.TASK_LISTS] ?: emptySet()
                prefs[SettingsKeys.TASK_LISTS] = currentSet + newItem
            }
        }
    }

    fun removeItem(item: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                val currentSet = prefs[SettingsKeys.TASK_LISTS] ?: emptySet()
                prefs[SettingsKeys.TASK_LISTS] = currentSet - item
            }
        }
    }
}