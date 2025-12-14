package io.novumd.gitseek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import io.novumd.gitseek.ui.bookmark.BottomTab
import io.novumd.gitseek.ui.search.SearchScreen
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val tabs = remember {
                listOf(
                    BottomTab.Search(), // startDestination
                    BottomTab.Bookmark()
                )
            }
            val (selectedTab, onSelectedTabChanged) = remember { mutableStateOf(tabs.first()) }

            LaunchedEffect(navController) {
                navController.currentBackStackEntryFlow.collectLatest { entry ->
                    val search = runCatching { entry.toRoute<BottomTab.Search>() }.getOrNull()
                    search?.let { onSelectedTabChanged(it) }

                    val bookmark = runCatching { entry.toRoute<BottomTab.Bookmark>() }.getOrNull()
                    bookmark?.let { onSelectedTabChanged(it) }
                }
            }

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        tabs.forEach { tab ->
                            NavigationBarItem(
                                selected = selectedTab == tab,
                                onClick = {
                                    onSelectedTabChanged(tab)
                                    navController.navigate(tab) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = stringResource(tab.labelRes)
                                    )
                                },
                                label = { Text(stringResource(tab.labelRes)) }
                            )
                        }
                    }
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = tabs.first(),
                    modifier = Modifier.padding(padding)
                ) {
                    composable<BottomTab.Search> {
                        SearchScreen {}
                    }
                    composable<BottomTab.Bookmark> {
                        Text("Bookmark Screen")
                    }
                }
            }
        }
    }
}
