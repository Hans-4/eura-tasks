package me.hannes.eura_tasks.ui.screens

import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddTask
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.hannes.eura_tasks.R
import me.hannes.eura_tasks.db.lists.ListDbEvent
import me.hannes.eura_tasks.db.lists.ListDbState
import me.hannes.eura_tasks.db.lists.systemTaskList
import me.hannes.eura_tasks.db.tasks.TaskDbEvent
import me.hannes.eura_tasks.db.tasks.TaskDbState
import me.hannes.eura_tasks.ui.Converter
import me.hannes.eura_tasks.ui.UiEvent
import me.hannes.eura_tasks.ui.UiState
import me.hannes.eura_tasks.ui.screens.homeScreenComponents.AddNewTaskListDialog
import me.hannes.eura_tasks.ui.screens.homeScreenComponents.AddTaskBottomSheet
import me.hannes.eura_tasks.ui.screens.homeScreenComponents.FabMenuItem
import me.hannes.eura_tasks.ui.screens.homeScreenComponents.SystemTaskLists
import me.hannes.eura_tasks.ui.screens.homeScreenComponents.UserTaskLists
import me.hannes.eura_tasks.ui.screens.homeScreenComponents.addNewTaskListComponents.ListWithSimilarNameWarningDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onUiEvent: (UiEvent) -> Unit,
    taskDbState: TaskDbState,
    listDbState: ListDbState,
    onTaskDbEvent: (TaskDbEvent) -> Unit,
    onListDbEvent: (ListDbEvent) -> Unit,
    onTaskList: (String) -> Unit,
    onSettings: () -> Unit,
    onSearch: () -> Unit,
    uiState: UiState,
    darkTheme: Boolean = isSystemInDarkTheme(),
) {
    val taskLists = listDbState.userLists

    val systemThemeIndex = if (darkTheme) 1 else 0

    val noUserList = taskLists.isEmpty()

    val topBarHeight = 100.dp

    val rotation by animateFloatAsState(
        targetValue = if (uiState.isHomeFABMenuExpanded) 45f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "FAB Rotation"
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage = stringResource(R.string.there_is_no_user_list_please_add_one_first)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState(),
        snapAnimationSpec = null //TODO
    )

    val tabItems = listOf("My Lists", "Tags")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.isAddingTask && noUserList) {
        if (uiState.isAddingTask && taskLists.isEmpty()) {
            snackbarHostState.showSnackbar(
                message = snackbarMessage,
                withDismissAction = true
            )
            onUiEvent(UiEvent.CloseAddTaskSheet)
        }
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column {
                    CenterAlignedTopAppBar(
                        modifier = Modifier.heightIn(max = topBarHeight),
                        title = {
                            Text(
                                "Tasks",
                                fontWeight = Bold
                            )
                        },
                        actions = {
                            IconButton(
                                modifier = Modifier.size(32.dp),
                                onClick = { onSettings() },
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
                        },
                        scrollBehavior = scrollBehavior,
                        colors = TopAppBarDefaults.topAppBarColors(
                            scrolledContainerColor = MaterialTheme.colorScheme.background
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { onSearch() },
                        colors = buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
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
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(32.dp),
                    dismissActionContentColor = MaterialTheme.colorScheme.onSurface
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
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    systemTaskList
                        .take(6)
                        .chunked(2)
                        .forEach { list ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (item in list) {
                                val icon = Converter.systemTypeConverter(item.type)
                                val color = Converter.colorStringConverter(
                                    systemThemeIndex = systemThemeIndex,
                                    colorString = item.colorString
                                )
                                val title = Converter.pageNameConverter(pageName = item.name)

                                val (completedTaskCount, totalTaskCount) = when (item.name) {
                                    "SYSTEM_ALL" -> Pair(
                                        taskDbState.tasks.filter { it.isCompleted }.size,
                                        taskDbState.tasks.size
                                    )
                                    "SYSTEM_FAVORITES" -> Pair(
                                        taskDbState.tasks.filter { it.isFavorite && it.isCompleted }.size,
                                        taskDbState.tasks.filter { it.isFavorite }.size
                                    )
                                    else -> Pair(0, 0)
                                }

                                val progress: Float = if (totalTaskCount == 0) {
                                    0f
                                } else {
                                    (completedTaskCount.toFloat() / totalTaskCount.toFloat())
                                }

                                Log.d("Ouput test", "Value: $progress, Completed: $completedTaskCount, All: $totalTaskCount")

                                Box(modifier = Modifier.weight(1f)) {
                                    SystemTaskLists(
                                        count = totalTaskCount,
                                        icon = icon,
                                        title = title,
                                        progress = progress,
                                        color = color,
                                        onTask = { onTaskList(item.name)}
                                    )
                                }
                            }
                            if (list.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            stickyHeader{
                SecondaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                ) {
                    tabItems.forEachIndexed { index, item ->
                        Tab(
                            selected = index == 0,
                            onClick = { selectedTabIndex = index },
                            text = { Text(item) }
                        )
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

                    if (!noUserList) {
                        Card(
                            shape = MaterialTheme.shapes.large,
                            border = BorderStroke(
                                width = 1.dp,
                                brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            taskLists.forEachIndexed { index, item ->
                                val icon = Converter.typeIconConverter(typeString = item.type)
                                val colorItem = Converter.colorStringConverter(
                                    systemThemeIndex = systemThemeIndex,
                                    colorString = item.colorString
                                )
                                val itemName = Converter.pageNameConverter(item.name)
                                val taskListCount = taskDbState.tasks.filter { it.taskList == item.name }.size

                                UserTaskLists(
                                    index = index,
                                    title = itemName,
                                    icon = icon,
                                    count = taskListCount,
                                    color = colorItem,
                                    onClick = { onTaskList(item.name) }
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No Lists",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 104.dp)
                                .fillMaxWidth()
                        )
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
                    .background(Color.Black.copy(alpha = 0.5f))
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
                onListDbEvent = onListDbEvent,
                listDbState = listDbState,
                onClick = {}
            )
        }

        if (!noUserList) {
            if (uiState.isAddingTask) {
                AddTaskBottomSheet(
                    onDbEvent = onTaskDbEvent,
                    onUiEvent = onUiEvent,
                    taskDbState = taskDbState,
                    uiState = uiState,
                    currentTab = "HOME_SCREEN",
                    firstUserTaskList = taskLists.first().name,
                    taskLists = taskLists
                )
            }
        }

        if (uiState.isListWithSimilarNameWarningDialogOpen) {
            ListWithSimilarNameWarningDialog(
                onUiEvent = onUiEvent
            )
        }

    }
}