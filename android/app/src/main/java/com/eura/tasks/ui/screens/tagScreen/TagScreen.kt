package com.eura.tasks.ui.screens.tagScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiState
import com.eura.tasks.ui.screens.tagScreen.components.ManageTagSheet
import com.eura.tasks.ui.screens.tagScreen.components.TaskTagItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagScreen(
    tagEntity: TagsEntity?,
    tasks: List<TaskEntity>,
    onClose: () -> Unit,
    onTaskDetails: (Int, String) -> Unit,
    onTaskDbEvent: (TaskDbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    uiState: UiState,
    onTagDbEvent: (TagDbEvent) -> Unit
) {
    val title = tagEntity?.title ?: "Tags"

    Scaffold(
        topBar = {
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { item ->
                TaskTagItem(
                    item = item,
                    onTaskDetails = onTaskDetails,
                    parentScreen = title,
                    onTaskDbEvent = onTaskDbEvent
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
                onTagDbEvent(TagDbEvent.DeleteTag(tagEntity!!))
                onUiEvent(UiEvent.CloseManageTagSheet)
            }
        )
    }
}