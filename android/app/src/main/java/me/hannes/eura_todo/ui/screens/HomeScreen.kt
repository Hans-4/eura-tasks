package me.hannes.eura_todo.ui.screens

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreens(

) {
    val tabs = listOf("My Tasks", "Recipes", "Movies", "Clean")
    // Wir berechnen die Gesamtanzahl der Tabs (Star + Listen + Add-Button)
    val totalTabs = 1 + tabs.size + 1

    val pagerState = rememberPagerState(pageCount = { totalTabs })

    // Coroutine Scope für das Klicken auf einen Tab
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(100.dp),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Red //MaterialTheme.colorScheme.background
                    ),
                    title = {
                        Box(
                            modifier = Modifier.fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Todos"
                            )
                        }
                    },
                    actions = {
                        Box(
                            modifier = Modifier.fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = {},
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color.Green
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
                PrimaryScrollableTabRow(
                        // Nutze direkt den State des Pagers
                        selectedTabIndex = pagerState.currentPage,
                edgePadding = 0.dp
                ) {
                // 1. Star Tab
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(0) }
                    },
                    icon = { Icon(Icons.Rounded.Star, null) }
                )

                // 2. Dynamische Tabs
                tabs.forEachIndexed { index, tabTitle ->
                    val targetPage = index + 1
                    Tab(
                        selected = pagerState.currentPage == targetPage,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(targetPage) }
                        },
                        text = { Text(tabTitle) }
                    )
                }

                // 3. Add Tab (Sollte vermutlich keine eigene Seite im Pager haben?)
                Tab(
                    selected = false,
                    onClick = { /* Logik für neue Liste */ },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Add, null)
                            Spacer(Modifier.width(4.dp))
                            Text("New list")
                        }
                    }
                )
            }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = Color.Red
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null
                )
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            modifier = Modifier.padding(innerPadding),
            state = pagerState
        ) {

        }
    }
}