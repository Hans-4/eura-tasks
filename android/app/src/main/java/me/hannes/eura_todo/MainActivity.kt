package me.hannes.eura_todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.room.Room
import me.hannes.eura_todo.db.TodoDatabase
import me.hannes.eura_todo.ui.screens.HomeScreens
import me.hannes.eura_todo.ui.theme.EuraToDoTheme
import me.hannes.eura_todo.ui.viewModels.TodoViewModel

class MainActivity : ComponentActivity() {

    private val todoDb by lazy {
        Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todos.db"
        )
            .build()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            EuraToDoTheme {
                HomeScreens()
            }
        }
    }
}
