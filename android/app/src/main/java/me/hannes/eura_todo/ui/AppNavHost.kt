package me.hannes.eura_todo.ui

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.hannes.eura_todo.db.DbState
import me.hannes.eura_todo.db.DbEvent
import me.hannes.eura_todo.ui.screens.HomeScreen
import me.hannes.eura_todo.ui.screens.TaskDetailsScreen
import me.hannes.eura_todo.ui.screens.TaskScreen


@Composable
fun AppNavHost(
    dbState: DbState,
    uiState: UiState,
    onDbEvent: (DbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable(
            route = "home",
            enterTransition = {
                slideIntoContainer(
                    towards = End,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = Start,
                    animationSpec = tween(300)
                )
            }
        ) {
            HomeScreen(
                dbState = dbState,
                uiState = uiState,
                onDbEvent = onDbEvent,
                onUiEvent = onUiEvent,
                onTask = { listName -> navController.navigate("taskLists/$listName")}
            )
        }

        composable(
            route = "taskLists/{listName}",
            enterTransition = {
                slideIntoContainer(
                    towards = Start,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = End,
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val listName = backStackEntry.arguments?.getString("listName").toString()
            TaskScreen(
                pageName = listName,
                dbState = dbState,
                uiState = uiState,
                onDbEvent = onDbEvent,
                onUiEvent = onUiEvent,
                onNavigateToHome = {navController.popBackStack()},
                onTaskDetails = { taskId -> navController.navigate("taskDetails/$taskId") },
            )
        }

        composable(
            route = "taskDetails/{taskId}",
            enterTransition = {
                slideInVertically(initialOffsetY = { it }) + fadeIn()
            },
            popExitTransition = {
                slideOutVertically(targetOffsetY = { it }) + fadeOut()
            }
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            TaskDetailsScreen(
                taskId = taskId,
                onDbEvent = onDbEvent,
                onClose = { navController.popBackStack() },
                uiState = uiState,
                onUiEvent = onUiEvent
            )
        }
    }
}