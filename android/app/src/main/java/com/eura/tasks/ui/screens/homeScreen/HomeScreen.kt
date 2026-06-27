package com.eura.tasks.ui.screens.homeScreen

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.eura.tasks.R
import com.eura.tasks.db.lists.ListDbEvent
import com.eura.tasks.db.lists.ListDbState
import com.eura.tasks.db.lists.systemTaskList
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagDbState
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.TaskDbState
import com.eura.tasks.ui.Converter
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiState
import com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.addList.AddNewTaskListDialog
import com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.addTask.AddTaskBottomSheet
import com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.FabMenuItem
import com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.SystemTaskLists
import com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.tagListColumn.TagListColumnItem
import com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.userListColumn.UserListColumnItem
import com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.addList.addListComponents.ItemWithSimilarNameWarningDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onTagDbEvent: (TagDbEvent) -> Unit,
    tagDbState: TagDbState,
    onUiEvent: (UiEvent) -> Unit,
    taskDbState: TaskDbState,
    listDbState: ListDbState,
    onTaskDbEvent: (TaskDbEvent) -> Unit,
    onListDbEvent: (ListDbEvent) -> Unit,

    onTaskList: (String) -> Unit,
    onTagList: (Int) -> Unit,
    onSettings: () -> Unit,
    onSearch: () -> Unit,
    uiState: UiState,
    darkTheme: Boolean = isSystemInDarkTheme(),
) {
    val taskLists = listDbState.userLists
    val noUserList = taskLists.isEmpty()

    val tagList = tagDbState.tags
    val noTags = tagList.isEmpty()

    val systemThemeIndex = if (darkTheme) 1 else 0


    val topBarHeight = 100.dp

    val rotation by animateFloatAsState(
        targetValue = if (uiState.isHomeFABMenuExpanded) 45f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "FAB Rotation"
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage = stringResource(R.string.there_is_no_user_list_please_add_one_first)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )

    val tabItems = listOf(stringResource(R.string.my_lists), stringResource(R.string.tags))

    val pagerState = rememberPagerState(pageCount = { tabItems.size })
    val coroutineScope = rememberCoroutineScope()

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
                .fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Column(modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 8.dp)) {
                    systemTaskList
                        .take(6)
                        .chunked(2)
                        .forEach { list ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (item in list) {
                                val icon = Converter.systemTypeConverter(item.type)
                                val color = Converter.colorStringConverter(
                                    systemThemeIndex = systemThemeIndex,
                                    colorString = item.colorString
                                )
                                val title = Converter.pageNameConverter(pageName = item.title)

                                val (completedTaskCount, totalTaskCount) = when (item.title) {
                                    "SYSTEM_ALL" -> Pair(
                                        taskDbState.tasks.filter { it.isCompleted }.size,
                                        taskDbState.tasks.size
                                    )
                                    "SYSTEM_FAVORITES" -> Pair(
                                        taskDbState.tasks.filter { it.isFavorite && it.isCompleted }.size,
                                        taskDbState.tasks.filter { it.isFavorite }.size
                                    )
                                    "SYSTEM_WITH_TAGS" -> Pair(
                                        taskDbState.tasks.filter { it.hasTags && it.isCompleted }.size,
                                        taskDbState.tasks.filter { it.hasTags }.size
                                    )
                                    else -> Pair(0, 0)
                                }

                                val progress: Float = if (totalTaskCount == 0) {
                                    0f
                                } else {
                                    (completedTaskCount.toFloat() / totalTaskCount.toFloat())
                                }

                                Box(modifier = Modifier.weight(1f)) {
                                    SystemTaskLists(
                                        count = totalTaskCount,
                                        icon = icon,
                                        title = title,
                                        progress = progress,
                                        color = color,
                                        onTask = { onTaskList(item.title)}
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

            //TODO: Change to stickyHeader
            item{
                SecondaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                ) {
                    tabItems.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                      },
                            text = { Text(title) }
                        )
                    }
                }
            }

            item {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) { page ->
                    when (page) {
                        0 -> UserListColumnItem(
                            noUserList = noUserList,
                            taskLists = taskLists,
                            systemThemeIndex = systemThemeIndex,
                            taskDbState = taskDbState,
                            onTaskList = onTaskList
                        )
                        1 -> TagListColumnItem(
                            noTags = noTags,
                            tagList = tagList,
                            onTagList = onTagList
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
                    onTagDbEvent = onTagDbEvent,
                    onUiEvent = onUiEvent,
                    taskDbState = taskDbState,
                    tagDbState = tagDbState,
                    uiState = uiState,
                    currentTab = "HOME_SCREEN",
                    firstUserTaskList = taskLists.first().title,
                    taskLists = taskLists
                )
            }
        }

        if (uiState.isItemWithSimilarNameWarningDialogOpen) {
            ItemWithSimilarNameWarningDialog(
                onUiEvent = onUiEvent,
                reason = uiState.similarNameWarningReason
            )
        }

    }
}