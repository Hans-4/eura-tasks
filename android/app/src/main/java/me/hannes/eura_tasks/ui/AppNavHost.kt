package me.hannes.eura_tasks.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
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
        val defaultEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
            slideIntoContainer(
                towards = Start,
                animationSpec = tween(300)
            )
        }

        val defaultExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
            slideOutOfContainer(
                towards = Start,
                animationSpec = tween(300)
            )
        }

        val defaultPopEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
            slideIntoContainer(
                towards = End,
                animationSpec = tween(300)
            )
        }

        val defaultPopExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
            slideOutOfContainer(
                towards = End,
                animationSpec = tween(300)
            )
        }

        composable(
            route = "home",
            enterTransition = {
                defaultEnterTransition()
            },
            exitTransition = {
                defaultExitTransition()
            },
            popEnterTransition = {
                defaultPopEnterTransition()
            },
            popExitTransition = {
                defaultPopExitTransition()
            }
        ) {
            HomeScreen(
                taskDbState = taskDbState,
                listDbState = listDbState,
                uiState = uiState,
                onTaskDbEvent = onTaskDbEvent,
                onListDbEvent = onListDbEvent,
                onUiEvent = onUiEvent,
                onTaskList = { listName -> navController.navigate("taskLists/$listName")},
                onSettings = { navController.navigate("settings")},
                onSearch = { navController.navigate("search") }
            )
        }

        composable(
            route = "settings",
            enterTransition = {
                defaultEnterTransition()
            },
            exitTransition = {
                defaultExitTransition()
            },
            popEnterTransition = {
                defaultPopEnterTransition()
            },
            popExitTransition = {
                defaultPopExitTransition()
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
                defaultEnterTransition()
            },
            exitTransition = {
                defaultExitTransition()
            },
            popEnterTransition = {
                defaultPopEnterTransition()
            },
            popExitTransition = {
                defaultPopExitTransition()
            }
        ) {
            SearchScreen(
                onClose = { navController.popBackStack() },
                taskDbState = taskDbState,
                onTaskDbEvent = onTaskDbEvent,
                onTaskDetails = { taskId, parentScreen ->
                    navController.navigate("taskDetails/$taskId/$parentScreen")
                }
            )
        }

        composable(
            route = "linkGoogleAccount",
            enterTransition = {
                defaultEnterTransition()
            },
            exitTransition = {
                defaultExitTransition()
            },
            popEnterTransition = {
                defaultPopEnterTransition()
            },
            popExitTransition = {
                defaultPopExitTransition()
            }
        ) {
            LinkGoogleAccountScreen(
                onClose = { navController.popBackStack() },
                onSuccess = { account ->
                    googleDriveViewModel.initDriveService(context, account)
                },
                onSignOut = {
                    googleDriveViewModel.clearDriveService()
                }
            )
        }

        composable(
            route = "taskLists/{listName}",
            enterTransition = {
                defaultEnterTransition()
            },
            exitTransition = {
                defaultExitTransition()
            },
            popEnterTransition = {
                defaultPopEnterTransition()
            },
            popExitTransition = {
                defaultPopExitTransition()
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
                onTaskDetails = { taskId, parentScreen -> navController.navigate("taskDetails/$taskId/$parentScreen") },
            )
        }

        composable(
            route = "taskDetails/{taskId}/{parentScreen}",
            enterTransition = {
                defaultEnterTransition()
            },
            exitTransition = {
                defaultExitTransition()
            },
            popEnterTransition = {
                defaultPopEnterTransition()
            },
            popExitTransition = {
                defaultPopExitTransition()
            }
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            val task = taskDbState.tasks.find { it.id == taskId }
            val parentScreen = backStackEntry.arguments?.getString("parentScreen").toString()
            if (task != null) {
                TaskDetailsScreen(
                    task = task,
                    onTaskDbEvent = onTaskDbEvent,
                    onClose = { navController.popBackStack() },
                    uiState = uiState,
                    onUiEvent = onUiEvent,
                    onTaskList = { listName -> navController.navigate("taskLists/$listName") },
                    parentScreen = parentScreen
                )
            }
        }
    }
}