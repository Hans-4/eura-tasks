package me.hannes.eura_tasks.ui

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.hannes.eura_tasks.db.lists.ListDbEvent
import me.hannes.eura_tasks.db.lists.ListDbState
import me.hannes.eura_tasks.db.tasks.TaskDbEvent
import me.hannes.eura_tasks.db.tasks.TaskDbState
import me.hannes.eura_tasks.ui.screens.HomeScreen
import me.hannes.eura_tasks.ui.screens.SearchScreen
import me.hannes.eura_tasks.ui.screens.SettingsScreen
import me.hannes.eura_tasks.ui.screens.TaskDetailsScreen
import me.hannes.eura_tasks.ui.screens.TaskScreen
import me.hannes.eura_tasks.ui.screens.homeScreenComponents.settingsChildrenScreens.LinkGoogleAccountScreen
import me.hannes.eura_tasks.ui.screens.sychronice.ListConflictWarningDialog
import me.hannes.eura_tasks.ui.viewModels.TaskDbViewModel
import me.hannes.eura_tasks.ui.viewModels.GoogleDriveViewModel
import me.hannes.eura_tasks.ui.viewModels.ListDbViewModel


@Composable
fun AppNavHost(
    taskDbState: TaskDbState,
    listDbState: ListDbState,
    uiState: UiState,
    onTaskDbEvent: (TaskDbEvent) -> Unit,
    onListDbEvent: (ListDbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    taskDbViewModel: TaskDbViewModel,
    listDbViewModel: ListDbViewModel,
    googleDriveViewModel: GoogleDriveViewModel
) {
    val navController = rememberNavController()

    val context = LocalContext.current

    val listConflict by googleDriveViewModel.listConflict.collectAsState()

    listConflict?.let { conflict ->
        ListConflictWarningDialog(
            localList = conflict.localList,
            remoteList = conflict.remoteList,
            onResolve = { resolved ->
                conflict.onResolved(resolved)
            }
        )
    }

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
                taskDbState = taskDbState,
                listDbState = listDbState,
                uiState = uiState,
                onTaskDbEvent = onTaskDbEvent,
                onListDbEvent = onListDbEvent,
                onUiEvent = onUiEvent,
                onTask = { listName -> navController.navigate("taskLists/$listName")},
                onSettings = { navController.navigate("settings")},
                onSearch = { navController.navigate("search") }
            )
        }

        composable(
            route = "settings",
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
        ) {
            SettingsScreen(
                onClose = { navController.popBackStack() },
                onLinkGoogleAccount = { navController.navigate("linkGoogleAccount")},
                listDbState = listDbState,
                listDbViewModel = listDbViewModel,
                taskDbViewModel = taskDbViewModel,
                googleDriveViewModel = googleDriveViewModel
            )
        }

        composable(
            route = "search",
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
        ) {
            SearchScreen(
                onClose = { navController.popBackStack() },
                taskDbState = taskDbState,
                onTaskDbEvent = onTaskDbEvent,
                onTaskDetails = { taskId ->
                    navController.navigate("taskDetails/$taskId")
                }
            )
        }

        composable(
            route = "linkGoogleAccount",
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
        ) {
            LinkGoogleAccountScreen(
                onClose = { navController.popBackStack() },
                onSuccess = { account ->
                    googleDriveViewModel.initDriveService(context, account)
                }
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
                taskDbState = taskDbState,
                listDbState = listDbState,
                uiState = uiState,
                onTaskDbEvent = onTaskDbEvent,
                onListDbEvent = onListDbEvent,
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
            val task = taskDbState.tasks.find { it.id == taskId }
            if (task != null) {
                TaskDetailsScreen(
                    task = task,
                    onTaskDbEvent = onTaskDbEvent,
                    onClose = { navController.popBackStack() },
                    uiState = uiState,
                    onUiEvent = onUiEvent
                )
            }
        }
    }
}