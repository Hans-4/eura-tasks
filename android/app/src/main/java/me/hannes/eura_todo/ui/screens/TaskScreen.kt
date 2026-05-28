package me.hannes.eura_todo.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import me.hannes.eura_todo.db.DbEvent
import me.hannes.eura_todo.db.DbState
import me.hannes.eura_todo.ui.UiEvent
import me.hannes.eura_todo.ui.UiState
import me.hannes.eura_todo.ui.pageNameConverter
import me.hannes.eura_todo.ui.screens.homeScreenComponents.AddNewTaskListDialog
import me.hannes.eura_todo.ui.screens.homeScreenComponents.AddTaskBottomSheet
import me.hannes.eura_todo.ui.screens.homeScreenComponents.SortItemsSheet
import me.hannes.eura_todo.ui.theme.blue
import me.hannes.eura_todo.ui.theme.green
import me.hannes.eura_todo.ui.theme.pink
import me.hannes.eura_todo.ui.theme.purple
import me.hannes.eura_todo.ui.theme.red
import me.hannes.eura_todo.ui.theme.yellow
import me.hannes.eura_todo.ui.viewModels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    onUiEvent: (UiEvent) -> Unit,
    onDbEvent: (DbEvent) -> Unit,
    onNavigateToHome: () -> Unit,
    onTaskDetails: (Int) -> Unit,
    uiState: UiState,
    dbState: DbState,
    settingsViewModel: SettingsViewModel = viewModel(),
    pageName: String,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val systemThemeIndex = if (darkTheme) 1 else 0

    val red = red[systemThemeIndex]
    val yellow = yellow[systemThemeIndex]
    val green = green[systemThemeIndex]
    val blue = blue[systemThemeIndex]
    val purple = purple[systemThemeIndex]
    val pink = pink[systemThemeIndex]

    val pageTitle = pageNameConverter(pageName = pageName)

    val taskLists by settingsViewModel.itemList.collectAsStateWithLifecycle(
        initialValue = SettingsViewModel.INITIAL_DIREKT_LIST
    )

    val pageList = taskLists.find { it.name == pageTitle }

    val pageColor = when(pageList?.colorString) {
        "red" -> red
        "yellow" -> yellow
        "green" -> green
        "blue" -> blue
        "pink" -> pink
        else -> purple
    }

    val tasksToShow = dbState.tasks.filter { it.taskList == pageTitle }

    Scaffold(
        topBar = {
            Column(

            ) {
                TopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {
                        Text("")
                    },
                    navigationIcon = {
                        Card(
                            shape = CircleShape,
                            elevation = CardDefaults.elevatedCardElevation(
                                10.dp
                            ),
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .size(40.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = White
                            )
                        ) {
                            IconButton(
                                onClick = { onNavigateToHome() },
                                shape = RoundedCornerShape(32.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBackIosNew,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    actions = {
                        Card(
                            shape = RoundedCornerShape(32.dp),
                            elevation = CardDefaults.elevatedCardElevation(10.dp),
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .width(96.dp)
                                .height(40.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = White
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    modifier = Modifier.size(32.dp),
                                    onClick = {TODO()},
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .size(30.dp),
                                        imageVector = Icons.Rounded.IosShare,
                                        contentDescription = null
                                    )
                                }

                                IconButton(
                                    modifier = Modifier.size(32.dp),
                                    onClick = {TODO()},
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .size(30.dp),
                                        imageVector = Icons.Rounded.MoreHoriz,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {onUiEvent(UiEvent.OpenAddTaskSheet)},
                containerColor = pageColor.primaryContainer,
                contentColor = pageColor.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = 0.99f)
                .drawWithContent {
                    drawContent()

                    val fadeBrush = Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.3f to Color.Transparent,
                        1.0f to Color.Black,
                        startY = 0f,
                        endY = 300f
                    )

                    drawRect(
                        brush = fadeBrush,
                        blendMode = BlendMode.DstIn
                    )
                }
            ,
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                    text = pageTitle,
                    fontSize = 32.sp,
                    fontWeight = Bold,
                    color = pageColor.primary
                )
            }
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
                                    pageTitle
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
                                    onClick = { onTaskDetails(task.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = pageColor.primaryContainer, //TODO: Search for alternative color. Original color: surfaceVariant
                                        contentColor = pageColor.onSurfaceVariant
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
                                                    color = pageColor.onSurfaceVariant
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
                                    onClick = { onTaskDetails(task.id) },
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

        if (uiState.isChangingSortType) {
            SortItemsSheet(
                onUiEvent = onUiEvent,
                onDbEvent = onDbEvent,
                dbState = dbState
            )
        }
        if (uiState.isAddingNewTaskList) {
            AddNewTaskListDialog(
                onUiEvent = onUiEvent,
                onClick = {}
            )
        }
        if (uiState.isAddingTask) {
            AddTaskBottomSheet(
                onDbEvent = onDbEvent,
                onUiEvent = onUiEvent,
                dbState = dbState,
                uiState = uiState,
                currentTab = pageTitle,
                firstTaskList = taskLists[0].name,
                taskLists = taskLists
            )
        }
    }
}