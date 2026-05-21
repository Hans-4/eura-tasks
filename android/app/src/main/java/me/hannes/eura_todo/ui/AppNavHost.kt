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
import me.hannes.eura_todo.db.TaskState
import me.hannes.eura_todo.db.TodoEvent
import me.hannes.eura_todo.ui.screens.AddTask
import me.hannes.eura_todo.ui.screens.HomeScreen


@Composable
fun AppNavHost(
    dbState: TaskState,
    onEvent: (TodoEvent) -> Unit
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
            HomeScreen(
                state = dbState,
                onEvent = onEvent,
                onNavigateToAdd = { navController.navigate("addTask") }
            )
        }

        composable(
            route = "addTask",
            enterTransition = {
                slideInVertically(initialOffsetY = { it }) + fadeIn()
            },
            popExitTransition = {
                slideOutVertically(targetOffsetY = { it }) + fadeOut()
            }
        ) {
            AddTask(onClose = { navController.popBackStack() })
        }
    }
}