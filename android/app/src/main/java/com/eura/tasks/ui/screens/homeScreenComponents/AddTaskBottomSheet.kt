package com.eura.tasks.ui.screens.homeScreenComponents

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
import com.eura.tasks.db.lists.UserListEntity
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagDbState
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.TaskDbState
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiState
import com.eura.tasks.ui.screens.homeScreenComponents.addTaskBottomSheetComponents.AddTaskScreen
import com.eura.tasks.ui.screens.homeScreenComponents.addTaskBottomSheetComponents.SelectTaskListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    onDbEvent: (TaskDbEvent) -> Unit,
    onTagDbEvent: (TagDbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    taskDbState: TaskDbState,
    tagDbState: TagDbState,
    uiState: UiState,
    currentTab: String,
    firstUserTaskList: String,
    taskLists: List<UserListEntity>
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
                        onTagDbEvent = onTagDbEvent,
                        onUiEvent = onUiEvent,
                        taskDbState = taskDbState,
                        tagDbState = tagDbState,
                        uiState = uiState,
                        currentTab = currentTab,
                        firstUserTaskList = firstUserTaskList,
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