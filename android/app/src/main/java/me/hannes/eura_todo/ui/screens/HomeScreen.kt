package me.hannes.eura_todo.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ShortText
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.hannes.eura_todo.db.SortType
import me.hannes.eura_todo.db.TaskState
import me.hannes.eura_todo.db.TodoEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onEvent: (TodoEvent) -> Unit,
    onNavigateToAdd: () -> Unit,
    state: TaskState
) {
    val tabs = listOf("My Tasks", "Recipes", "Movies", "Clean")

    val totalTabs = 1 + tabs.size + 1

    val pagerState = rememberPagerState(pageCount = { totalTabs })

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column{
                CenterAlignedTopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(110.dp),
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

                tabs.forEachIndexed { index, tabTitle ->
                    val targetPage = index + 1
                    Tab(
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selected = pagerState.currentPage == targetPage,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(targetPage) }
                        },
                        text = { Text(tabTitle) }
                    )
                }

                Tab(
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selected = false,
                    onClick = {},
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Add, null)
                            Spacer(Modifier.width(4.dp))
                            Text("New list")
                        }
                    }
                )
            }
            }
        },
        bottomBar = {
            if (state.isAddingTask) {
                ModalBottomSheet(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding(),
                    onDismissRequest = {onEvent(TodoEvent.CloseSheet)},
                    dragHandle = null
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = state.todoTitle,
                            onValueChange = {
                                onEvent(TodoEvent.SetTodoTitle(it))
                            },
                            placeholder = {
                                Text(text = "Title")
                            }
                        )
                        TextField(
                            value = state.todoDescription,
                            onValueChange = {
                                onEvent(TodoEvent.SetTodoDescription(it))
                            },
                            placeholder = {
                                Text(text = "Description")
                            }
                        )

                        Row(

                        ) {
                            IconButton(
                                onClick = {}
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ShortText,
                                    contentDescription = "Add description button"
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            TextButton(
                                onClick = {onEvent(TodoEvent.SaveTask)}
                            ) {
                                Text(
                                    "Save"
                                )
                            }
                        }
                    }
                }
            }
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {onEvent(TodoEvent.OpenSheet)},
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

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SortType.values().forEach { sortType ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onEvent(TodoEvent.SortTodos(sortType))
                                },
                            verticalAlignment = CenterVertically
                        ) {
                            RadioButton(
                                selected = state.sortType == sortType,
                                onClick = {
                                    onEvent(TodoEvent.SortTodos(sortType))
                                }
                            )
                            Text(text = sortType.name)
                        }
                    }
                }
            }
            items(state.tasks) { task ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = task.title,
                            fontSize = 20.sp
                        )
                        Text(
                            text = task.description
                        )
                    }
                    IconButton(onClick = {
                        onEvent(TodoEvent.DeleteTodo(task))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete contact"
                        )
                    }
                }
            }
        }
    }
}