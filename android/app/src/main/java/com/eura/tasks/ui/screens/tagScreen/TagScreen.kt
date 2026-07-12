package com.eura.tasks.ui.screens.tagScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.TaskDbState
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiState
import com.eura.tasks.ui.screens.tagScreen.components.ManageTagSheet
import com.eura.tasks.ui.screens.tagScreen.components.TaskItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagScreen(
    tagEntity: TagsEntity,
    onClose: () -> Unit,
    onTaskDetails: (String, String) -> Unit,
    onTaskDbEvent: (TaskDbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    uiState: UiState,
    onTagDbEvent: (TagDbEvent) -> Unit,

    taskDbState: TaskDbState
) {
    val tabs = listOf("Checked", "Unchecked")
    var selectedTab by remember { mutableIntStateOf(0) }


    val title = tagEntity.title

    val tasks = taskDbState.tasks
    val taskTags = taskDbState.taskTags

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val (checked, unchecked) = remember(tasks, taskTags) {
        val checkedIds = taskTags.filter { it.isActive }.map { it.taskUuid }.toSet()
        tasks.sortedBy { it.title }.partition { it.taskUuid in checkedIds }
    }

    // 2. Sync tab selection with scroll position when scrolling stops
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            selectedTab = if (checked.isNotEmpty() && listState.firstVisibleItemIndex < checked.size) 0 else 1
        }
    }

    LaunchedEffect(tagEntity.tagUuid) {
        onTaskDbEvent(TaskDbEvent.GetAllTasksByTagUuid(tagEntity.tagUuid))
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { onClose() }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBackIosNew,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { onUiEvent(UiEvent.OpenManageTagSheet) },
                            shape = CircleShape
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(30.dp),
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = null,
                            )
                        }
                    },
                    title = {
                        Text(title)
                    }
                )

                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = index == selectedTab,
                            onClick = {
                                selectedTab = index
                                // 3. Scroll to the corresponding section
                                scope.launch {
                                    val targetIndex = if (index == 0) 0 else checked.size
                                    listState.animateScrollToItem(targetIndex)
                                }
                            },
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(text = title)
                        }
                    }
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
            items(checked) { item ->
                TaskItem(
                    item = item,
                    onTaskDetails = onTaskDetails,
                    parentScreen = title,
                    onTaskDbEvent = onTaskDbEvent,

                    tagEntity = tagEntity,
                    taskTags = taskTags
                )
            }
            items(unchecked) { item ->
                TaskItem(
                    item = item,
                    onTaskDetails = onTaskDetails,
                    parentScreen = title,
                    onTaskDbEvent = onTaskDbEvent,

                    tagEntity = tagEntity,
                    taskTags = taskTags
                )
            }

            item {
                if (tasks.isEmpty()) {
                    Text(
                        text = "No linked tasks",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 200.dp)
                    )
                }
            }
        }
    }

    if (uiState.isManageTagSheetOpen) {
        ManageTagSheet(
            onDismiss = { onUiEvent(UiEvent.CloseManageTagSheet) },
            onDeletedTag = {
                onClose()
                onTagDbEvent(TagDbEvent.DeleteTag(tagEntity))
                onUiEvent(UiEvent.CloseManageTagSheet)
            }
        )
    }
}