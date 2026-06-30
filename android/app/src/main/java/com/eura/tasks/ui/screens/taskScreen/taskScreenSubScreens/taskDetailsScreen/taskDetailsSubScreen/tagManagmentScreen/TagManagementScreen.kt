package com.eura.tasks.ui.screens.taskScreen.taskScreenSubScreens.taskDetailsScreen.taskDetailsSubScreen.tagManagmentScreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
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
import androidx.compose.ui.unit.dp
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagDbState
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiState
import com.eura.tasks.ui.screens.taskScreen.taskScreenSubScreens.taskDetailsScreen.taskDetailsSubScreen.tagManagmentScreen.components.AddTagDialog
import com.eura.tasks.ui.screens.taskScreen.taskScreenSubScreens.taskDetailsScreen.taskDetailsSubScreen.tagManagmentScreen.components.TagItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManagementScreen(
    onClose: () -> Unit,
    task: TaskEntity,

    tagDbState: TagDbState,
    onTagDbEvent: (TagDbEvent) -> Unit,

    uiState: UiState,
    onUiEvent: (UiEvent) -> Unit
) {
    val tabs = listOf("Checked", "Unchecked")
    var selectedTab by remember { mutableIntStateOf(0) }

    // 1. Initialize scroll state and scope
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val taskTags = tagDbState.taskTags
    val tags = tagDbState.tags

    val (checked, unchecked) = remember(tags, taskTags) {
        val checkedIds = taskTags.map { it.tagId }.toSet()
        tags.sortedBy { it.title }.partition { it.id in checkedIds }
    }

    // 2. Sync tab selection with scroll position when scrolling stops
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            selectedTab = if (checked.isNotEmpty() && listState.firstVisibleItemIndex < checked.size) 0 else 1
        }
    }

    BackHandler(enabled = true, onBack = { onClose() })

    LaunchedEffect(task.id) {
        onTagDbEvent(TagDbEvent.GetAllTagsByTaskId(task.id))
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
                                contentDescription = "Back"
                            )
                        }
                    },
                    title = { Text("Tag management") },
                    actions = {
                        IconButton(onClick = { onUiEvent(UiEvent.OpenAddTagsDialog) }) {
                            Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                        }
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
            state = listState,
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(checked, key = { it.id }) { item ->
                TagItem(tag = item, taskTag = taskTags, task = task, onTagDbEvent = onTagDbEvent)
            }
            items(unchecked, key = { it.id }) { item ->
                TagItem(tag = item, taskTag = taskTags, task = task, onTagDbEvent = onTagDbEvent)
            }
        }
    }

    if (uiState.isAddTagsDialogOpen) {
        AddTagDialog(
            onConfirm = { onTagDbEvent(TagDbEvent.SaveTagInTask(task.id, task.uuid)) },
            onDismiss = { onUiEvent(UiEvent.CloseAddTagsDialog) },

            tagDbState = tagDbState,
            onTagDbEvent = onTagDbEvent
        )
    }
}