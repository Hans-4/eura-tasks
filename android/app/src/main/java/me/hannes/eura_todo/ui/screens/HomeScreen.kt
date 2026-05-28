package me.hannes.eura_todo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddTask
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.first
import me.hannes.eura_todo.R
import me.hannes.eura_todo.db.DbEvent
import me.hannes.eura_todo.db.DbState
import me.hannes.eura_todo.ui.Converter
import me.hannes.eura_todo.ui.UiEvent
import me.hannes.eura_todo.ui.UiState
import me.hannes.eura_todo.ui.screens.homeScreenComponents.AddNewTaskListDialog
import me.hannes.eura_todo.ui.screens.homeScreenComponents.AddTaskBottomSheet
import me.hannes.eura_todo.ui.screens.homeScreenComponents.FabMenuItem
import me.hannes.eura_todo.ui.screens.homeScreenComponents.SystemTaskLists
import me.hannes.eura_todo.ui.screens.homeScreenComponents.UserTaskLists
import me.hannes.eura_todo.ui.theme.ColorItems
import me.hannes.eura_todo.ui.theme.blue
import me.hannes.eura_todo.ui.theme.green
import me.hannes.eura_todo.ui.theme.pink
import me.hannes.eura_todo.ui.theme.purple
import me.hannes.eura_todo.ui.theme.red
import me.hannes.eura_todo.ui.theme.yellow
import me.hannes.eura_todo.ui.viewModels.SettingsViewModel

data class SystemTaskListsItems(
    val name: String,
    val count: Int,
    val icon: ImageVector,
    val listType: String,
    val progress: Float,
    val color: ColorItems
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onUiEvent: (UiEvent) -> Unit,
    onDbEvent: (DbEvent) -> Unit,
    onTask: (String) -> Unit,
    uiState: UiState,
    dbState: DbState,
    settingsViewModel: SettingsViewModel = viewModel(),
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val systemThemeIndex = if (darkTheme) 1 else 0

    val red = red[systemThemeIndex]
    val yellow = yellow[systemThemeIndex]
    val green = green[systemThemeIndex]
    val blue = blue[systemThemeIndex]
    val purple = purple[systemThemeIndex]
    val pink = pink[systemThemeIndex]

    val topBarHeight = 110.dp

    val rotation by animateFloatAsState(
        targetValue = if (uiState.isHomeFABMenuExpanded) 45f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "FAB Rotation"
    )

    val taskLists by settingsViewModel.itemList.collectAsStateWithLifecycle(
        initialValue = SettingsViewModel.INITIAL_DIREKT_LIST
    )

    val totalTabs = 1 + taskLists.size

    val pagerState = rememberPagerState(pageCount = { totalTabs })

    LaunchedEffect(Unit) {
        val savedIndex = settingsViewModel.selectedListIndex.first()
        if (savedIndex > 0) {
            snapshotFlow { pagerState.pageCount }.first { it > savedIndex }
            pagerState.scrollToPage(savedIndex)
        }
        snapshotFlow { pagerState.currentPage }.collect { currentPage ->
            settingsViewModel.setSelectedListIndex(currentPage)
        }
    }


    Scaffold(
        topBar = {
            Column{
                CenterAlignedTopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(topBarHeight),
                    colors = TopAppBarDefaults.topAppBarColors(
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
                                .padding(end = 16.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center,
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

                Button(
                    onClick = {TODO()},
                    colors = buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSecondary,
                        contentColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            "Search in Tasks..."
                        )
                    }
                }
            }

            //Box to close the FAB menu over the entire top bar
            AnimatedVisibility(
                visible = uiState.isHomeFABMenuExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(Color.Black.copy(alpha = 0.1f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onUiEvent(UiEvent.CloseHomeFABMenu)
                        }
                )

            }
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedVisibility(
                    visible = uiState.isHomeFABMenuExpanded,
                    enter = fadeIn() + expandVertically() + slideInVertically { it / 2 },
                    exit = fadeOut() + shrinkVertically() + slideOutVertically { it / 2 }
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FabMenuItem(
                            label = "New List",
                            icon = Icons.Rounded.Checklist,
                            onClick = { onUiEvent(UiEvent.OpenAddTaskListDialog) },
                            onUiEvent = onUiEvent
                        )
                        FabMenuItem(
                            label = "New Task",
                            icon = Icons.Rounded.AddTask,
                            onClick = { onUiEvent(UiEvent.OpenAddTaskSheet) },
                            onUiEvent = onUiEvent
                        )
                    }
                }
                FloatingActionButton(
                    onClick = {
                        if (uiState.isHomeFABMenuExpanded) {
                            onUiEvent(UiEvent.CloseHomeFABMenu)
                        } else {
                            onUiEvent(UiEvent.OpenHomeFABMenu)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                val itemList = listOf(
                    SystemTaskListsItems(
                        name = taskLists[0].name,
                        count = 14,
                        icon = Icons.Rounded.Today,
                        listType = "Today",
                        progress = 0.1f,
                        color = purple
                    ),
                    SystemTaskListsItems(
                        name = taskLists[1].name,
                        count = 28,
                        icon = Icons.Rounded.Event,
                        listType = "Scheduled",
                        progress = 0.3f,
                        color = pink
                    ),
                    SystemTaskListsItems(
                        name = taskLists[2].name,
                        count = 89,
                        icon = Icons.AutoMirrored.Rounded.List,
                        listType = "All",
                        progress = 0.8f,
                        color = red
                    ),
                    SystemTaskListsItems(
                        name = taskLists[3].name,
                        count = 3,
                        icon = Icons.Rounded.Star,
                        listType = "Favorites",
                        progress = 0.4f,
                        color = yellow
                    ),
                    SystemTaskListsItems(
                        name = taskLists[4].name,
                        count = 1,
                        icon = Icons.Rounded.PersonAdd,
                        listType = "Assigned to me",
                        progress = 0.2f,
                        color = green
                    ),
                    SystemTaskListsItems(
                        name = taskLists[5].name,
                        count = 19,
                        icon = Icons.Rounded.ShoppingCart,
                        listType = "Groceries",
                        progress = 0.5f,
                        color = blue
                    ),
                )
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    itemList.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (item in rowItems) {
                                Box(modifier = Modifier.weight(1f)) {
                                    SystemTaskLists(
                                        count = item.count,
                                        icon = item.icon,
                                        listType = item.listType,
                                        progress = item.progress,
                                        color = item.color,
                                        onTask = { onTask(item.name)}
                                    )
                                }
                            }
                            if (rowItems.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.my_lists),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        shape = MaterialTheme.shapes.large,
                        border = BorderStroke(
                            width = 1.dp,
                            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        taskLists.drop(6).forEachIndexed { index, item ->
                            val icon = Converter.typeIconConverter(typeString = item.type)
                            val colorItems = Converter.colorStringConverter(item.colorString)
                            val colorItem = colorItems[systemThemeIndex]
                            UserTaskLists(
                                index = index,
                                title = item.name,
                                icon = icon,
                                count = 33,
                                color = colorItem,
                                onClick = { onTask(item.name) }
                            )
                        }
                    }
                }
            }
        }


        //Box to close the FAB menu over the entire screen body
        AnimatedVisibility(
            visible = uiState.isHomeFABMenuExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.1f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onUiEvent(UiEvent.CloseHomeFABMenu)
                    }
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
                currentTab = "HOME_SCREEN",
                firstUserTaskList = taskLists[6].name,
                taskLists = taskLists
            )
        }
    }
}