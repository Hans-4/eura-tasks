package com.eura.tasks.ui.screens.searchScreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eura.tasks.R
import com.eura.tasks.db.SearchEvent
import com.eura.tasks.db.SearchFilter
import com.eura.tasks.db.SearchState
import com.eura.tasks.db.lists.ListDbState
import com.eura.tasks.db.lists.UserListEntity
import com.eura.tasks.db.tags.TagDbState
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.TaskDbState
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiState
import com.eura.tasks.ui.screens.searchScreen.components.ListItem
import com.eura.tasks.ui.screens.searchScreen.components.SearchFilterBottomSheet
import com.eura.tasks.ui.screens.searchScreen.components.TagItem
import com.eura.tasks.ui.screens.searchScreen.components.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onClose: () -> Unit,
    taskDbState: TaskDbState,
    listDbState: ListDbState,
    tagDbState: TagDbState,
    uiState: UiState,
    onTaskDbEvent: (TaskDbEvent) -> Unit,
    onTaskDetails: (Int, String) -> Unit,
    onSearchEvent: (SearchEvent) -> Unit,
    searchState: SearchState,
    onUiEvent: (UiEvent) -> Unit,

    onTaskList: (String) -> Unit,
    onTagDetails: (Int, String) -> Unit,
) {
    var textValue by remember { mutableStateOf(taskDbState.searchQuery) }

    val searchFilter = searchState.searchFilter

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(textValue) {
        onTaskDbEvent(TaskDbEvent.SetSearchQuery(textValue))
    }

    val filteredItems = remember(searchState.searchQuery, searchState.searchFilter, taskDbState.tasks, listDbState.userLists, tagDbState.tags) {
        when (searchFilter) {
            SearchFilter.TASK -> taskDbState.tasks.filter {
                searchState.searchQuery.isBlank() ||
                        it.title.contains(searchState.searchQuery, ignoreCase = true) ||
                        it.description.contains(searchState.searchQuery, ignoreCase = true)
            }
            SearchFilter.LIST -> listDbState.userLists.filter {
                searchState.searchQuery.isBlank() ||
                        it.title.contains(searchState.searchQuery, ignoreCase = true)
            }
            SearchFilter.TAG -> tagDbState.tags.filter {
                searchState.searchQuery.isBlank() ||
                        it.title.contains(searchState.searchQuery, ignoreCase = true)
            }
        }
    }

    BackHandler(
        enabled = true,
        onBack = onClose
    )

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp),
                    ) {
                        IconButton(
                            onClick = { onClose() },
                            shape = MaterialTheme.shapes.extraLarge,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBackIosNew,
                                contentDescription = "Back"
                            )
                        }
                    }

                },
                title = {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            BasicTextField(
                                value = textValue,
                                onValueChange = {
                                    textValue = it
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp
                                ),
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.CenterStart,
                                    ) {
                                        Text(
                                            text = if (textValue.isEmpty()) "${stringResource(R.string.search_here)}..." else "",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            fontSize = 16.sp,
                                        )
                                        innerTextField()
                                    }
                                },
                                singleLine = true
                            )

                            if (textValue.isNotEmpty()) {
                                IconButton(
                                    onClick = { textValue = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Clear search",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                },

                actions = {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp),
                    ) {
                        IconButton(
                            onClick = { onUiEvent(UiEvent.OpenSetSearchFilterBottomSheet) },
                            shape = MaterialTheme.shapes.extraLarge,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FilterList,
                                contentDescription = null
                            )
                        }
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
            items(filteredItems, key = { item ->
                when (item) {
                    is TaskEntity -> "task_${item.id}"
                    is UserListEntity -> "list_${item.id}"
                    is TagsEntity -> "tag_${item.id}"
                    else -> item.hashCode()
                }
            }) { item ->
                when (searchFilter) {
                    SearchFilter.TASK -> {
                        val taskItem = item as? TaskEntity
                        if (taskItem != null) {
                            TaskItem(
                                item = taskItem,
                                onTaskDetails = onTaskDetails,
                                onTaskDbEvent = onTaskDbEvent
                            )
                        }
                    }

                    SearchFilter.LIST -> {
                        val listItem = item as? UserListEntity
                        if (listItem != null) {
                            ListItem(
                                item = listItem,
                                onTaskList = onTaskList
                            )
                        }
                    }

                    SearchFilter.TAG -> {
                        val tagItem = item as? TagsEntity
                        if (tagItem != null) {
                            TagItem(
                                item = tagItem,
                                onTagDetails = onTagDetails,
                            )
                        }
                    }
                }
            }

            item {
                if (filteredItems.isEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 104.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "No tasks found",
                        )
                    }
                }
            }
        }
    }

    if (uiState.isSetSearchFilterBottomSheetOpen) {
        SearchFilterBottomSheet(
            onSearchEvent = onSearchEvent,
            searchState = searchState,
            onDismiss = { onUiEvent(UiEvent.CloseSetSearchFilterBottomSheet) }
        )
    }
}