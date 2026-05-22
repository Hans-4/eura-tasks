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
import me.hannes.eura_todo.ui.screens.AddTask
import me.hannes.eura_todo.ui.screens.HomeScreen


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
            HomeScreen(
                dbState = dbState,
                uiState = uiState,
                onDbEvent = onDbEvent,
                onUiEvent = onUiEvent,
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