package me.hannes.eura_todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import me.hannes.eura_todo.ui.screens.HomeScreens
import me.hannes.eura_todo.ui.theme.EuraToDoTheme

class MainActivity : ComponentActivity() {
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