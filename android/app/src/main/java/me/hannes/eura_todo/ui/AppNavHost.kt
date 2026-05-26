package me.hannes.eura_todo.ui

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
import me.hannes.eura_todo.ui.screens.TaskScreen
import me.hannes.eura_todo.ui.screens.TaskDetailsScreen


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
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            TaskScreen(
                dbState = dbState,
                uiState = uiState,
                onDbEvent = onDbEvent,
                onUiEvent = onUiEvent,
                onTaskDetails = { taskId -> navController.navigate("taskDetails/$taskId") }
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