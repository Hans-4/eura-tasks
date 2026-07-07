package com.eura.tasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eura.tasks.db.AppDatabase
import com.eura.tasks.ui.AppNavHost
import com.eura.tasks.notifications.FullDayNotificationService
import com.eura.tasks.ui.theme.EuraTasksTheme
import com.eura.tasks.ui.viewModels.GoogleDriveViewModel
import com.eura.tasks.ui.viewModels.ListDbViewModel
import com.eura.tasks.ui.viewModels.RepeatDbViewModel
import com.eura.tasks.ui.viewModels.SearchViewModel
import com.eura.tasks.ui.viewModels.TagDbViewModel
import com.eura.tasks.ui.viewModels.TaskDbViewModel
import com.eura.tasks.ui.viewModels.UiViewModel

class MainActivity : ComponentActivity() {

    private val db by lazy {
        AppDatabase.getDatabase(applicationContext)
    }

    private val taskDbViewModel by viewModels<TaskDbViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TaskDbViewModel(db.taskDao, db.tagDao) as T
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

    private val tagDbViewModel by viewModels<TagDbViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TagDbViewModel(tagDao = db.tagDao) as T
                }
            }
        }
    )

    private val repeatDbViewModel by viewModels<RepeatDbViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RepeatDbViewModel(repeatDao = db.repeatDao) as T
                }
            }
        }
    )

    private val searchViewModel by viewModels<SearchViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(
                        taskDao = db.taskDao,
                        listDao = db.listDao,
                        tagDao = db.tagDao
                    ) as T
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
                    return GoogleDriveViewModel(taskDao = db.taskDao, listDao = db.listDao, tagDao = db.tagDao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val service = FullDayNotificationService(applicationContext)

        enableEdgeToEdge()

        setContent {
            EuraTasksTheme {
                val taskState by taskDbViewModel.state.collectAsState()
                val listState by listDbViewModel.state.collectAsState()
                val tagState by tagDbViewModel.state.collectAsState()
                val repeatState by repeatDbViewModel.state.collectAsState()

                val searchState by searchViewModel.state.collectAsState()

                val uiState by uiViewModel.state.collectAsState()

                AppNavHost(
                    onTaskDbEvent = { event ->
                        taskDbViewModel.onEvent(
                            event,
                            repeatDbViewModel::onEvent,
                            repeatState
                        ) },
                    taskDbState = taskState,

                    listDbState = listState,
                    tagDbState = tagState,

                    onRepeatDbEvent = repeatDbViewModel::onEvent,
                    repeatDbState = repeatState,

                    searchState = searchState,
                    uiState = uiState,

                    onListDbEvent = { event -> listDbViewModel.onEvent(event, uiViewModel::onEvent) },
                    onTagDbEvent = { event -> tagDbViewModel.onEvent(event, uiViewModel::onEvent) },
                    onSearchEvent = searchViewModel::onEvent,
                    onUiEvent = uiViewModel::onEvent,
                    taskDbViewModel = taskDbViewModel,
                    listDbViewModel = listDbViewModel,
                    googleDriveViewModel = googleDriveViewModel,

                    service = service
                )
            }
        }
    }

}
