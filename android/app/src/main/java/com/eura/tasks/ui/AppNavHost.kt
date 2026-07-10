package com.eura.tasks.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.eura.tasks.db.SearchEvent
import com.eura.tasks.db.SearchState
import com.eura.tasks.db.lists.ListDbEvent
import com.eura.tasks.db.lists.ListDbState
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagDbState
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.TaskDbState
import com.eura.tasks.db.tasks.repeats.RepeatDbEvent
import com.eura.tasks.db.tasks.repeats.RepeatDbState
import com.eura.tasks.ui.globalComponents.ListConflictWarningDialog
import com.eura.tasks.ui.screens.homeScreen.HomeScreen
import com.eura.tasks.ui.screens.searchScreen.SearchScreen
import com.eura.tasks.ui.screens.settingsScreen.SettingsScreen
import com.eura.tasks.ui.screens.settingsScreen.settingsSubScreens.LinkGoogleAccountScreen
import com.eura.tasks.ui.screens.tagScreen.TagScreen
import com.eura.tasks.ui.screens.taskScreen.TaskScreen
import com.eura.tasks.ui.screens.taskScreen.taskScreenSubScreens.taskDetailsScreen.TaskDetailsScreen
import com.eura.tasks.ui.screens.taskScreen.taskScreenSubScreens.taskDetailsScreen.taskDetailsSubScreen.tagManagmentScreen.TagManagementScreen
import com.eura.tasks.ui.viewModels.GoogleDriveViewModel
import com.eura.tasks.ui.viewModels.ListDbViewModel
import com.eura.tasks.ui.viewModels.TaskDbViewModel


@Composable
fun AppNavHost(
    onTaskDbEvent: (TaskDbEvent) -> Unit,
    taskDbState: TaskDbState,

    onListDbEvent: (ListDbEvent) -> Unit,
    listDbState: ListDbState,

    onTagDbEvent: (TagDbEvent) -> Unit,
    tagDbState: TagDbState,

    onRepeatDbEvent: (RepeatDbEvent) -> Unit,
    repeatDbState: RepeatDbState,

    onSearchEvent: (SearchEvent) -> Unit,
    searchState: SearchState,

    onUiEvent: (UiEvent) -> Unit,
    uiState: UiState,
    taskDbViewModel: TaskDbViewModel,
    listDbViewModel: ListDbViewModel,
    googleDriveViewModel: GoogleDriveViewModel,
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

    val animDuration = 300

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        val defaultEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(animDuration)
            ) + fadeIn(animationSpec = tween(animDuration))
        }

        val defaultExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(animDuration)
            ) + fadeOut(animationSpec = tween(animDuration))
        }

        val defaultPopEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(animDuration)
            ) + fadeIn(animationSpec = tween(animDuration))
        }

        val defaultPopExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(animDuration)
            ) + fadeOut(animationSpec = tween(animDuration))
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
                tagDbState = tagDbState,
                onTagDbEvent = onTagDbEvent,
                taskDbState = taskDbState,
                listDbState = listDbState,
                uiState = uiState,
                onTaskDbEvent = onTaskDbEvent,
                onListDbEvent = onListDbEvent,
                onUiEvent = onUiEvent,

                onRepeatDbEvent = onRepeatDbEvent,
                repeatDbState = repeatDbState,


                onTaskList = { listId, listName -> navController.navigate("taskLists/$listId/$listName")},
                onTagList = { tagId -> navController.navigate("tagList/$tagId")},
                onSettings = { navController.navigate("settings")},
                onSearch = { navController.navigate("search") },
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
                listDbViewModel = listDbViewModel,
                taskDbViewModel = taskDbViewModel,
                googleDriveViewModel = googleDriveViewModel,
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

                uiState = uiState,
                onSearchEvent = onSearchEvent,
                searchState = searchState,
                onUiEvent = onUiEvent,
                listDbState = listDbState,
                tagDbState = tagDbState,

                onTaskDetails = { taskId, parentScreen ->
                    navController.navigate("taskDetails/$taskId/$parentScreen")
                },
                onTaskList = { listName -> navController.navigate("taskLists/$listName") },
                onTagDetails = { tagId -> navController.navigate("tagList/$tagId") }
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
                googleDriveViewModel = googleDriveViewModel,
                onClose = { navController.popBackStack() },
                onSuccess = { account ->
                    googleDriveViewModel.initDriveService(context, account)
                },
                onSignOut = {
                    googleDriveViewModel.clearDriveService()
                },
                onUiEvent = onUiEvent,
                uiState = uiState
            )
        }

        composable(
            route = "taskLists/{listId}/{listName}",
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
            val listName = backStackEntry.arguments?.getString("listName")
            val listId = backStackEntry.arguments?.getString("listId")
            TaskScreen(
                tagDbState = tagDbState,
                onTagDbEvent = onTagDbEvent,
                pageData = Pair(listId ?: "", listName ?: "Error"),
                taskDbState = taskDbState,
                listDbState = listDbState,
                uiState = uiState,
                onTaskDbEvent = onTaskDbEvent,
                onListDbEvent = onListDbEvent,
                onUiEvent = onUiEvent,
                onClose = {navController.popBackStack()},
                onTaskDetails = { taskId, parentScreen -> navController.navigate("taskDetails/$taskId/$parentScreen") },

                onRepeatDbEvent = onRepeatDbEvent,
                repeatDbState = repeatDbState
            )
        }

        composable(
            route = "taskDetails/{taskId}/{parentScreen}",
            deepLinks = listOf(
                navDeepLink { uriPattern = "euratasks://taskdetails/{taskId}/{parentScreen}" }
            ),
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType },
                navArgument("parentScreen") { defaultValue = "notification" }
            ),
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
            val taskId = backStackEntry.arguments?.getString("taskId")
            val task = taskDbState.tasks.find { it.taskUuid == taskId }
            val parentScreen = backStackEntry.arguments?.getString("parentScreen").toString()
            if (task != null) {
                TaskDetailsScreen(
                    task = task,
                    onTaskDbEvent = onTaskDbEvent,
                    onClose = { navController.popBackStack() },
                    uiState = uiState,
                    onUiEvent = onUiEvent,
                    onTaskList = { listName -> navController.navigate("taskLists/$listName") },
                    parentScreen = parentScreen,
                    onTagDbEvent = onTagDbEvent,
                    tagDbState = tagDbState,

                    taskDbState = taskDbState,

                    onRepeatDbEvent = onRepeatDbEvent,
                    repeatDbState = repeatDbState,

                    onTagManagement = { navController.navigate("tagManagement/${taskId}") }
                )
            }
        }

        composable(
            route = "tagManagement/{taskId}",
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
            val taskId = backStackEntry.arguments?.getString("taskId")
            val task = taskDbState.tasks.find { it.taskUuid == taskId }
            if (task != null) {
                TagManagementScreen(
                    onClose = { navController.popBackStack() },
                    task = task,

                    tagDbState = tagDbState,
                    onTagDbEvent = onTagDbEvent,

                    uiState = uiState,
                    onUiEvent = onUiEvent,

                    onTagList = { tagId -> navController.navigate("tagList/$tagId") }
                )
            }
        }

        composable(
            route = "tagList/{tagId}",
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
            val tagId = backStackEntry.arguments?.getString("tagId")
            val tagEntity = tagDbState.tags.find { it.tagUuid == tagId }
            onTaskDbEvent(TaskDbEvent.GetTaskById(tagId!!))
            if (tagEntity != null) {
                TagScreen(
                    tagEntity = tagEntity,
                    onClose = { navController.popBackStack() },
                    onTaskDetails = { taskId, parentScreen -> navController.navigate("taskDetails/$taskId/$parentScreen") },
                    onTaskDbEvent = onTaskDbEvent,
                    onUiEvent = onUiEvent,
                    uiState = uiState,
                    onTagDbEvent = onTagDbEvent,
                    taskDbState = taskDbState
                )
            }
        }
    }
}