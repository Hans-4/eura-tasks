package me.hannes.eura_todo.ui.screens

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.hannes.eura_todo.R
import me.hannes.eura_todo.db.DbState
import me.hannes.eura_todo.db.DbEvent
import me.hannes.eura_todo.ui.UiEvent
import me.hannes.eura_todo.ui.UiState
import me.hannes.eura_todo.ui.screens.homeScreenComponents.AddNewTaskListDialog
import me.hannes.eura_todo.ui.screens.homeScreenComponents.AddTaskBottomSheet
import me.hannes.eura_todo.ui.screens.homeScreenComponents.SortItemsSheet
import me.hannes.eura_todo.ui.viewModels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onUiEvent: (UiEvent) -> Unit,
    onDbEvent: (DbEvent) -> Unit,
    onTaskDetails: (Int) -> Unit,
    uiState: UiState,
    dbState: DbState,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val task_lists by settingsViewModel.itemList.collectAsStateWithLifecycle(
        initialValue = SettingsViewModel.INITIAL_LIST
    )

    val totalTabs = 1 + task_lists.size

    val pagerState = rememberPagerState(pageCount = { totalTabs })

    val scope = rememberCoroutineScope()

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
                        .height(110.dp),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
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
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {onUiEvent(UiEvent.OpenAddTaskSheet)},
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                .fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(task_lists) { index, list ->
                Text(list)
            }
        }
        if (uiState.isAddingNewTaskList) {
            AddNewTaskListDialog(
                onUiEvent = onUiEvent
            )
        }
    }
}