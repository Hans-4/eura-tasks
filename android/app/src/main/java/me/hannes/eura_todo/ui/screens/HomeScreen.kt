package me.hannes.eura_todo.ui.screens

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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.ShortText
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.hannes.eura_todo.db.DbState
import me.hannes.eura_todo.db.DbEvent
import me.hannes.eura_todo.ui.UiEvent
import me.hannes.eura_todo.ui.UiState
import me.hannes.eura_todo.ui.screens.homeScreenComponents.SortItemsSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onUiEvent: (UiEvent) -> Unit,
    onDbEvent: (DbEvent) -> Unit,
    onNavigateToAdd: () -> Unit,
    uiState: UiState,
    dbState: DbState
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
            if (dbState.isAddingTask) {
                ModalBottomSheet(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding(),
                    onDismissRequest = {onDbEvent(DbEvent.CloseSheet)},
                    dragHandle = null
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = dbState.todoTitle,
                            onValueChange = {
                                onDbEvent(DbEvent.SetTodoTitle(it))
                            },
                            placeholder = {
                                Text(text = "Title")
                            }
                        )
                        TextField(
                            value = dbState.todoDescription,
                            onValueChange = {
                                onDbEvent(DbEvent.SetTodoDescription(it))
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
                                onClick = {onDbEvent(DbEvent.SaveTask)}
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
                onClick = {onDbEvent(DbEvent.OpenSheet)},
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {onUiEvent(UiEvent.OpenSortItemSheet)}
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SwapVert,
                        contentDescription = "Change sort type"
                    )
                }

                IconButton(
                    onClick = {TODO()}
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "More options"
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dbState.tasks) { task ->
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {TODO()}
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                onClick = {onDbEvent(DbEvent.SetIsCompleted(!task.isCompleted))},
                                selected = task.isCompleted
                            )

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
                            IconButton(
                                onClick = {
                                    onDbEvent(DbEvent.SetTodoIsFavorite(!task.isFavorite))
                                }
                            ) {
                                if (task.isFavorite) {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = "Tod is favorite"
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Tod is not favorite"
                                    )
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
                uiState = uiState,
                dbState = dbState
            )
        }
    }
}