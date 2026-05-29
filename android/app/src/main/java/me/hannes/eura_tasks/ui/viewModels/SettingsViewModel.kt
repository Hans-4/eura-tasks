package me.hannes.eura_tasks.ui.viewModels

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class TaskList(
    val name: String,
    val type: String,
    val colorString: String,
)

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dataStore = application.dataStore

    companion object {
        val INITIAL_INDIREKT_LIST = setOf(
            "SYSTEM_TODAY|TODAY|purple",
            "SYSTEM_SCHEDULE|SCHEDULE|pink",
            "SYSTEM_ALL|ALL|red",
            "SYSTEM_FAVORITES|FAVORITES|yellow",
            "SYSTEM_ASSIGNED_TO_ME|ASSIGNED_TO_ME|green",
            "SYSTEM_GROCERIES|GROCERIES|blue",
            "MY_TASKS|OTHER|purple"
        )
        val INITIAL_DIREKT_LIST = listOf(
            TaskList(
                name = "SYSTEM_TODAY",
                type = "TODAY",
                colorString= "purple",
            ),
            TaskList(
                name = "SYSTEM_SCHEDULE",
                type = "SCHEDULE",
                colorString = "pink",
            ),
            TaskList(
                name = "SYSTEM_ALL",
                type = "ALL",
                colorString = "red",
            ),
            TaskList(
                name = "SYSTEM_FAVORITES",
                type = "FAVORITES",
                colorString = "yellow",
            ),
            TaskList(
                name = "SYSTEM_ASSIGNED_TO_ME",
                type = "ASSIGNED_TO_ME",
                colorString = "green",
            ),
            TaskList(
                name = "SYSTEM_GROCERIES",
                type = "GROCERIES",
                colorString = "blue",
            ),
            TaskList(
                name = "MY_TASKS",
                type = "OTHER",
                colorString = "purple"
            )
        )
    }

    val itemList: Flow<List<TaskList>> = dataStore.data.map { prefs ->
        val rawSet = prefs[SettingsKeys.TASK_LISTS] ?: INITIAL_INDIREKT_LIST.toSet()

        rawSet.map { entry ->
            if (entry.contains("|")) {
                val parts = entry.split("|")
                TaskList(name = parts[0], type = parts[1], colorString = parts[2])
            } else {
                TaskList(name = entry, type = "OTHER", colorString = "purple")
            }
        }
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

    fun addItem(name: String, type: String, color: String) {
        viewModelScope.launch {
            val entry = "$name|$type|$color"

            dataStore.edit { prefs ->
                val currentSet = prefs[SettingsKeys.TASK_LISTS] ?: INITIAL_INDIREKT_LIST.toSet()
                prefs[SettingsKeys.TASK_LISTS] = currentSet + entry
            }
        }
    }

    fun removeItem(taskList: TaskList) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                val currentSet = prefs[SettingsKeys.TASK_LISTS] ?: INITIAL_INDIREKT_LIST.toSet()
                val entryToRemove = "${taskList.name}|${taskList.type}|${taskList.colorString}"
                prefs[SettingsKeys.TASK_LISTS] = currentSet - entryToRemove
            }
        }
    }
}