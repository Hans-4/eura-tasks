package me.hannes.eura_tasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import me.hannes.eura_tasks.db.TodoDatabase
import me.hannes.eura_tasks.ui.AppNavHost
import me.hannes.eura_tasks.ui.theme.EuraToDoTheme
import me.hannes.eura_tasks.ui.viewModels.UiViewModel
import me.hannes.eura_tasks.ui.viewModels.DbViewModel
import me.hannes.eura_tasks.ui.viewModels.GoogleDriveViewModel

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "tasks.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    private val dbViewModel by viewModels<DbViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DbViewModel(db.dao) as T
                }
            }
        }
    )

    private val uiViewModel by viewModels<UiViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UiViewModel() as T
                }
            }
        }
    )

    private val googleDriveViewModel by viewModels<GoogleDriveViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            EuraToDoTheme {
                val state by dbViewModel.state.collectAsState()
                val uiState by uiViewModel.state.collectAsState()
                AppNavHost(
                    dbState = state,
                    uiState = uiState,
                    onDbEvent = dbViewModel::onEvent,
                    onUiEvent = uiViewModel::onEvent,
                    dbViewModel = dbViewModel,
                    googleDriveViewModel = googleDriveViewModel
                )
            }
        }
    }
}
