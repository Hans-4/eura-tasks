package me.hannes.eura_todo.ui.screens.homeScreenComponents

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.hannes.eura_todo.db.DbEvent
import me.hannes.eura_todo.db.DbState
import me.hannes.eura_todo.ui.UiEvent
import me.hannes.eura_todo.ui.UiState
import me.hannes.eura_todo.ui.screens.homeScreenComponents.AddTaskBottomSheetComponents.AddTaskScreen
import me.hannes.eura_todo.ui.screens.homeScreenComponents.AddTaskBottomSheetComponents.SelectTaskListScreen
import me.hannes.eura_todo.ui.viewModels.TaskList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    onDbEvent: (DbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    dbState: DbState,
    uiState: UiState,
    currentTab: String,
    firstTaskList: String,
    taskLists: List<TaskList>
) {
    ModalBottomSheet(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding(),
        onDismissRequest = { onUiEvent(UiEvent.CloseAddTaskSheet) },
        dragHandle = null
    ) {
        val sheetNavController = rememberNavController()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(animationSpec = tween(300))
        ) {
            NavHost(
                navController = sheetNavController,
                startDestination = "addTaskScreen",
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopStart
            ) {
                composable(
                    route = "addTaskScreen",
                    enterTransition = {
                        slideIntoContainer(
                            animationSpec = tween(300),
                            towards = End
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            animationSpec = tween(300),
                            towards = Start
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            animationSpec = tween(300),
                            towards = End
                        )
                    }
                ) {
                    AddTaskScreen(
                        onDbEvent = onDbEvent,
                        onUiEvent = onUiEvent,
                        dbState = dbState,
                        uiState = uiState,
                        currentTab = currentTab,
                        firstTaskList = firstTaskList,
                        onNavigateToSelectTaskListScreen = { sheetNavController.navigate("selectTaskListScreen") },
                        taskLists = taskLists
                    )
                }

                composable(
                    "selectTaskListScreen",
                    enterTransition = {
                        slideIntoContainer(
                            animationSpec = tween(300),
                            towards = Start
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            animationSpec = tween(300),
                            towards = End
                        )
                    }
                ) {
                    SelectTaskListScreen(
                        onDbEvent = onDbEvent,
                        onUiEvent = onUiEvent,
                        taskLists = taskLists,
                        onNavigateBackToAddTaskScreen = { sheetNavController.popBackStack() }
                    )
                }
            }
        }
    }
}