package com.eura.tasks.ui.screens.taskScreen.taskScreenSubScreens.taskDetailsScreenComponents

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagDbState
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiState
import com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.addTask.addTaskComponents.AddTagsDialog
import com.eura.tasks.ui.screens.taskScreen.taskScreenSubScreens.taskDetailsScreenComponents.tagManagmentScreenComponentes.TagItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManagementScreen(
    onClose: () -> Unit,
    task: TaskEntity,
    tagDbState: TagDbState,
    onTagDbEvent: (TagDbEvent) -> Unit,
    uiState: UiState,
    onTaskDbEvent: (TaskDbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit
) {
    val taskTags = tagDbState.tagsFromCurrentTask

    BackHandler(
        enabled = true,
        onBack = { onClose() }
    )

    LaunchedEffect(taskTags) {
        onTagDbEvent(TagDbEvent.GetAllTagsByUuid(task.uuid))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onClose() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Text("Tag management")
                },
                actions = {
                    IconButton(
                        onClick = { onUiEvent(UiEvent.OpenAddTagsDialog) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(taskTags) { item ->
                TagItem(item = item)
            }
        }
    }

    if (uiState.isAddTagsDialogOpen) {
        AddTagsDialog(
            onClose = { onUiEvent(UiEvent.CloseAddTagsDialog) },
            onTagDbEvent = onTagDbEvent,
            tagDbState = tagDbState,
            onTaskDbEvent = onTaskDbEvent,
            onUiEvent = onUiEvent,
            uiState = uiState
        )
    }
}