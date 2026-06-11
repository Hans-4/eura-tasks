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
import me.hannes.eura_tasks.db.Database
import me.hannes.eura_tasks.ui.AppNavHost
import me.hannes.eura_tasks.ui.theme.EuraTasksTheme
import me.hannes.eura_tasks.ui.viewModels.UiViewModel
import me.hannes.eura_tasks.ui.viewModels.TaskDbViewModel
import me.hannes.eura_tasks.ui.viewModels.GoogleDriveViewModel
import me.hannes.eura_tasks.ui.viewModels.ListDbViewModel
import kotlin.getValue

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            "database.db"
        )
            .build()
    }

    private val taskDbViewModel by viewModels<TaskDbViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TaskDbViewModel(db.taskDao) as T
                }
            }
        }
    )

    private val listDbViewModel by viewModels<ListDbViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ListDbViewModel(taskDao = db.taskDao, listDao = db.listDao) as T
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

    private val googleDriveViewModel by viewModels<GoogleDriveViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GoogleDriveViewModel(taskDao = db.taskDao, listDao = db.listDao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            EuraTasksTheme {
                val taskState by taskDbViewModel.state.collectAsState()
                val listState by listDbViewModel.state.collectAsState()
                val uiState by uiViewModel.state.collectAsState()
                AppNavHost(
                    taskDbState = taskState,
                    listDbState = listState,
                    uiState = uiState,
                    onTaskDbEvent = taskDbViewModel::onEvent,
                    onListDbEvent = { event -> listDbViewModel.onEvent(event, uiViewModel::onEvent) },
                    onUiEvent = uiViewModel::onEvent,
                    taskDbViewModel = taskDbViewModel,
                    listDbViewModel = listDbViewModel,
                    googleDriveViewModel = googleDriveViewModel
                )
            }
        }
    }
}
