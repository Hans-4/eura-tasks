package me.hannes.eura_todo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import me.hannes.eura_todo.R
import me.hannes.eura_todo.db.DbState
import me.hannes.eura_todo.db.DbEvent
import me.hannes.eura_todo.ui.UiEvent
import me.hannes.eura_todo.ui.UiState
import me.hannes.eura_todo.ui.screens.homeScreenComponents.AddNewTaskListDialog
import me.hannes.eura_todo.ui.screens.homeScreenComponents.AddTaskBottomSheet
import me.hannes.eura_todo.ui.screens.homeScreenComponents.SortItemsSheet
import me.hannes.eura_todo.ui.viewModels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onUiEvent: (UiEvent) -> Unit,
    onDbEvent: (DbEvent) -> Unit,
    onNavigateToAdd: () -> Unit,
    uiState: UiState,
    dbState: DbState,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val task_lists by settingsViewModel.itemList.collectAsStateWithLifecycle(
        initialValue = SettingsViewModel.INITIAL_LIST
    )

    val totalTabs = 1 + task_lists.size

    val pagerState = rememberPagerState(pageCount = { totalTabs })

    val scope = rememberCoroutineScope()


    Scaffold(
        topBar = {
            Column{
                CenterAlignedTopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    title = {
                        Box(
                            modifier = Modifier.fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Tasks",
                                fontWeight = Bold
                            )
                        }
                    },
                    actions = {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                modifier = Modifier.size(32.dp),
                                onClick = {},
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    imageVector = Icons.Rounded.AccountCircle,
                                    contentDescription = "Account"
                                )
                            }
                        }
                    }
                )

                PrimaryScrollableTabRow(
                    modifier = Modifier.height(56.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 0.dp
                ) {
                Tab(
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(0) }
                    },
                    icon = { Icon(Icons.Rounded.Star, null) }
                )

                task_lists.forEachIndexed { index, tabTitle ->
                    val targetPage = index + 1

                    val finalTabTitle = if (tabTitle == "My Tasks") {
                        stringResource(R.string.my_tasks)
                    } else {
                        tabTitle
                    }

                    Tab(
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selected = pagerState.currentPage == targetPage,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(targetPage) }
                        },
                        text = { Text(finalTabTitle) }
                    )
                }

                Tab(
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selected = false,
                    onClick = {onUiEvent(UiEvent.OpenAddTaskListDialog)},
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add, null
                            )
                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )
                            Text(
                                stringResource(R.string.new_list)
                            )
                        }
                    }
                )
            }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {onUiEvent(UiEvent.OpenAddTaskSheet)},
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null
                )
            }
        }
    ) { innerPadding ->
        val currentTabName = if (pagerState.currentPage == 0) {
            "FAVOURITES"
        } else {
            task_lists.getOrNull(pagerState.currentPage - 1) ?: "ERROR"
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) { page ->
            val pageTabName = if (page == 0) "FAVOURITES" else task_lists.getOrNull(page - 1) ?: "ERROR"

            val tasksToShow = if (page == 0) {
                dbState.tasks.filter { it.isFavorite }
            } else {
                dbState.tasks.filter { it.taskList == pageTabName }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        if (page == 0) "Marked" else task_lists.getOrNull(page - 1) ?: "Tasks",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Spacer(Modifier.weight(1f))

                                    IconButton(
                                        onClick = { onUiEvent(UiEvent.OpenSortItemSheet) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.SwapVert,
                                            contentDescription = "Change sort type"
                                        )
                                    }

                                    IconButton(
                                        onClick = { TODO() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.MoreVert,
                                            contentDescription = "More options"
                                        )
                                    }
                                }

                                tasksToShow.filter { !it.isCompleted }.forEach { task ->
                                    Button(
                                        onClick = {TODO()},
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                onClick = {
                                                    onDbEvent(DbEvent.SetIsCompleted(!task.isCompleted, task))
                                                },
                                                selected = task.isCompleted
                                            )
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = task.title, fontSize = 18.sp)
                                                if (task.description.isNotBlank()) {
                                                    Text(
                                                        text = task.description,
                                                        fontSize = 14.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = {
                                                    onDbEvent(DbEvent.SetTodoIsFavorite(!task.isFavorite, task))
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (task.isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                                    contentDescription = "Toggle favorite",
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Completed",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Spacer(Modifier.weight(1f))

                                    IconButton(
                                        onClick = { TODO() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.MoreVert,
                                            contentDescription = "More options"
                                        )
                                    }
                                }

                                tasksToShow.filter { it.isCompleted }.forEach { task ->
                                    Button(
                                        onClick = {TODO()},
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                onClick = {
                                                    onDbEvent(DbEvent.SetIsCompleted(!task.isCompleted, task))
                                                },
                                                selected = task.isCompleted
                                            )
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = task.title, fontSize = 18.sp)
                                                if (task.description.isNotBlank()) {
                                                    Text(
                                                        text = task.description,
                                                        fontSize = 14.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = {
                                                    onDbEvent(DbEvent.SetTodoIsFavorite(!task.isFavorite, task))
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (task.isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                                    contentDescription = "Toggle favorite"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (uiState.isChangingSortType) {
            SortItemsSheet(
                onUiEvent = onUiEvent,
                onDbEvent = onDbEvent,
                dbState = dbState
            )
        }
        if (uiState.isAddingNewTaskList) {
            AddNewTaskListDialog(
                onUiEvent = onUiEvent
            )
        }
        if (uiState.isAddingTask) {
            AddTaskBottomSheet(
                onDbEvent = onDbEvent,
                onUiEvent = onUiEvent,
                dbState = dbState,
                currentTab = currentTabName,
                firstTaskList = task_lists[0],
                taskLists = task_lists
            )
        }
    }
}