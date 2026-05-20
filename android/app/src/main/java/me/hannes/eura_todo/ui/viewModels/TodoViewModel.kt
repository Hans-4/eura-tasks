package me.hannes.eura_todo.ui.viewModels

import androidx.lifecycle.ViewModel
import me.hannes.eura_todo.db.TodoDao

class TodoViewModel(
    private val todoDao: TodoDao
): ViewModel() {
}