package com.eura.tasks.ui.screens.taskScreen.taskScreenSubScreens.taskDetailsScreenComponents

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagDbState
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.ui.screens.taskScreen.taskScreenSubScreens.taskDetailsScreenComponents.tagManagmentScreenComponentes.TagItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManagementScreen(
    onClose: () -> Unit,
    task: TaskEntity,
    tagDbState: TagDbState,
    onTagDbEvent: (TagDbEvent) -> Unit,
) {
    val taskTags = tagDbState.taskTags

    val tags = tagDbState.tags

    val tabs = listOf("Selected", "Unselected")

    var selectedTab by remember { mutableIntStateOf(0) }

    BackHandler(
        enabled = true,
        onBack = { onClose() }
    )

    LaunchedEffect(taskTags) {
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
                            onClick = { TODO() }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null
                            )
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
                            onClick = { selectedTab = index },
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
            items(tags) { item ->
                TagItem(
                    tag = item,
                    taskTag = taskTags,
                    task = task,

                    onTagDbEvent = onTagDbEvent
                )
            }
        }
    }
}