package me.hannes.eura_todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import me.hannes.eura_todo.db.TodoDatabase
import me.hannes.eura_todo.ui.screens.HomeScreens
import me.hannes.eura_todo.ui.theme.EuraToDoTheme
import me.hannes.eura_todo.ui.viewModels.TodoViewModel

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todos"
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel by viewModels<TodoViewModel> {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return TodoViewModel(db.todoDao()) as T
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            EuraToDoTheme {
                HomeScreens(viewModel)
            }
        }
    }
}
