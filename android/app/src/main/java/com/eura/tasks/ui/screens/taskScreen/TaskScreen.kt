package com.eura.tasks.ui.screens.taskScreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.eura.tasks.R
import com.eura.tasks.db.lists.ListDbEvent
import com.eura.tasks.db.lists.ListDbState
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagDbState
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.TaskDbState
import com.eura.tasks.db.tasks.repeats.RepeatDbEvent
import com.eura.tasks.db.tasks.repeats.RepeatDbState
import com.eura.tasks.ui.Converter
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiState
import com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.addList.AddNewTaskListDialog
import com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.addTask.AddTaskBottomSheet
import com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.SortItemsSheet
import com.eura.tasks.ui.screens.taskScreen.taskScreenComponents.DeleteWarningDialog
import com.eura.tasks.ui.screens.taskScreen.taskScreenComponents.ManageListSheet
import com.eura.tasks.ui.screens.taskScreen.taskScreenComponents.RenameListDialog
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    onTagDbEvent: (TagDbEvent) -> Unit,
    tagDbState: TagDbState,
    onUiEvent: (UiEvent) -> Unit,
    onTaskDbEvent: (TaskDbEvent) -> Unit,
    onListDbEvent: (ListDbEvent) -> Unit,
    onClose: () -> Unit,
    onTaskDetails: (String, String) -> Unit,
    uiState: UiState,
    taskDbState: TaskDbState,
    listDbState: ListDbState,

    pageId: String,

    darkTheme: Boolean = isSystemInDarkTheme(),

    onRepeatDbEvent: (RepeatDbEvent) -> Unit,
    repeatDbState: RepeatDbState
) {
    val systemThemeIndex = if (darkTheme) 1 else 0

    val scope = rememberCoroutineScope()

    LaunchedEffect(pageId, listDbState.userLists) {
        onListDbEvent(ListDbEvent.GetListsById(pageId))
    }

    val pageList = listDbState.currentListName
    val pageName = pageList?.title ?: ""

    val pageTitle = Converter.pageNameConverter(pageName = pageName)

    val taskLists = listDbState.userLists

    val pageColor = Converter.colorStringConverter(
        systemThemeIndex = systemThemeIndex,
        colorString = pageList?.colorString
    )

    val tasksToShow = when (pageName) {
        "SYSTEM_TODAY" -> taskDbState.tasks.filter { it.notificationTime?.toLocalDateTime(TimeZone.currentSystemDefault())?.date == Clock.System.todayIn(TimeZone.currentSystemDefault()) }
        "SYSTEM_SCHEDULE" -> taskDbState.tasks.filter { it.notificationTime != null }
        "SYSTEM_ALL" -> taskDbState.tasks
        "SYSTEM_FAVORITES" ->  taskDbState.tasks.filter { it.isFavorite }
        "SYSTEM_WITH_TAGS" -> taskDbState.tasks.filter { it.hasTags }
        else -> taskDbState.tasks.filter { it.parentListId == pageId }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage = stringResource(R.string.there_is_no_user_list_please_add_one_first)

    var isCompletedTasksExpanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isCompletedTasksExpanded) 270f else 90f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "Arrow rotation"
    )

    LaunchedEffect(uiState.isAddingTask) {
        if (uiState.isAddingTask && taskLists.isEmpty()) {
            snackbarHostState.showSnackbar(
                message = snackbarMessage,
                withDismissAction = true
            )
            onUiEvent(UiEvent.CloseAddTaskSheet)
        }
    }

    BackHandler(
        enabled = true,
        onBack = { onClose() }
    )

    Scaffold(
        topBar = {
            Column{
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
                        IconButton(
                            onClick = { onClose() },
                            shape = RoundedCornerShape(32.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBackIosNew,
                                contentDescription = null,
                                tint = pageColor.primary
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { onUiEvent(UiEvent.OpenManageListSheet) },
                            shape = CircleShape
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(30.dp),
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = null,
                                tint = pageColor.primary
                            )
                        }
                    }
                )
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
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    pageTitle
                                )

                                IconButton(
                                    onClick = { onUiEvent(UiEvent.OpenSortItemSheet) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.SwapVert,
                                        contentDescription = "Change sort type"
                                    )
                                }
                            }

                            tasksToShow.filter { !it.isCompleted }.forEach { task ->
                                Button(
                                    onClick = { onTaskDetails(task.taskUuid, pageTitle) },
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
                                                onTaskDbEvent(TaskDbEvent.SetIsCompleted(!task.isCompleted, task))
                                            },
                                            selected = task.isCompleted
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = task.title, fontSize = 18.sp)
                                            if (task.description?.isNotBlank() ?: false) {
                                                Text(
                                                    text = task.description,
                                                    fontSize = 14.sp,
                                                    color = pageColor.onSurfaceVariant
                                                )
                                            }
                                        }
                                        IconButton(
                                            onClick = {
                                                onTaskDbEvent(TaskDbEvent.SetTodoIsFavorite(!task.isFavorite, task))
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
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Completed",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                IconButton(
                                    onClick = { isCompletedTasksExpanded = !isCompletedTasksExpanded }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowBackIosNew,
                                        contentDescription = "Expand",
                                        modifier = Modifier
                                            .size(20.dp)
                                            .rotate(rotation)
                                    )
                                }
                            }

                            AnimatedVisibility(visible = isCompletedTasksExpanded) {
                                tasksToShow.filter { it.isCompleted }.forEach { task ->
                                    Button(
                                        onClick = { onTaskDetails(task.taskUuid, pageTitle) },
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
                                                    onTaskDbEvent(
                                                        TaskDbEvent.SetIsCompleted(
                                                            !task.isCompleted,
                                                            task
                                                        )
                                                    )
                                                },
                                                selected = task.isCompleted
                                            )
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = task.title, fontSize = 18.sp)
                                                if (task.description?.isNotBlank() ?: false) {
                                                    Text(
                                                        text = task.description,
                                                        fontSize = 14.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = {
                                                    onTaskDbEvent(
                                                        TaskDbEvent.SetTodoIsFavorite(
                                                            !task.isFavorite,
                                                            task
                                                        )
                                                    )
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
                onDbEvent = onTaskDbEvent,
                taskDbState = taskDbState
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
        if (taskLists.isNotEmpty()) {
            if (uiState.isAddingTask) {
                AddTaskBottomSheet(
                    onTagDbEvent = onTagDbEvent,
                    tagDbState = tagDbState,
                    onDbEvent = onTaskDbEvent,
                    onUiEvent = onUiEvent,
                    taskDbState = taskDbState,
                    uiState = uiState,
                    currentTab = pageName,
                    firstUserTaskList = taskLists.first().title,
                    taskLists = taskLists,

                    onRepeatDbEvent = onRepeatDbEvent,
                    repeatDbState = repeatDbState
                )
            }
        }
        if (uiState.isManageListSheetOpen) {
            ManageListSheet(
                pageName = pageName,
                onDeleteAllCompletedTasks = { onUiEvent(UiEvent.OpenDeleteAllCompletedTasksWarningDialog) },
                onRenameList = { onUiEvent(UiEvent.OpenRenameListDialog) },
                onDeleteList = { onUiEvent(UiEvent.OpenDeleteAllTasksWarningDialog) },
                onDismiss = { onUiEvent(UiEvent.CloseManageListSheet) }
            )
        }

        if (uiState.isDeleteAllCompletedTasksWarningDialogOpen) {
            DeleteWarningDialog(
                title = "Warning",
                text = "Are you sure you want to delete all completed tasks from this list?",

                onConfirm = {
                    pageList?.let {
                        onTaskDbEvent(TaskDbEvent.DeleteAllCompletedTasksByListId(pageId))
                        scope.launch {
                            onUiEvent(UiEvent.CloseDeleteAllCompletedTasksWarningDialog)
                            onUiEvent(UiEvent.CloseManageListSheet)
                        }
                    }
                },
                onDismiss = {onUiEvent(UiEvent.CloseDeleteAllCompletedTasksWarningDialog)},
            )
        }
        if (uiState.isRenameListDialogOpen) {
            RenameListDialog(
                listTitle = pageName,
                onDismiss = { onUiEvent(UiEvent.CloseRenameListDialog) },
                onConfirm = {
                    onListDbEvent(ListDbEvent.RenameList(pageId))
                    onUiEvent(UiEvent.CloseRenameListDialog)
                    onUiEvent(UiEvent.CloseManageListSheet)
                },

                onListDbEvent = onListDbEvent,
            )
        }
        if (uiState.isDeleteAllTasksWarningDialogOpen) {
            DeleteWarningDialog(
                title = "Warning",
                text = "Are you sure you want to delete all tasks from this list? This will also delete all Tasks in it.",

                onConfirm = {
                    pageList?.let {
                        onListDbEvent(ListDbEvent.DeleteListById(pageId))
                        scope.launch {
                            onUiEvent(UiEvent.CloseDeleteAllTasksWarningDialog)
                            onUiEvent(UiEvent.CloseManageListSheet)
                            delay(300.milliseconds)
                            onClose()
                        }
                    }
                },
                onDismiss = {onUiEvent(UiEvent.CloseDeleteAllTasksWarningDialog)}
            )
        }
    }
}