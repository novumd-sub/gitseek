package io.novumd.gitseek

import android.net.Uri
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import io.novumd.gitseek.domain.model.Repo
import io.novumd.gitseek.ui.bookmark.BookmarkScreen
import io.novumd.gitseek.ui.bookmark.BottomTab
import io.novumd.gitseek.ui.components.preventMultipleClick
import io.novumd.gitseek.ui.detail.DetailScreen
import io.novumd.gitseek.ui.search.SearchScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

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
                                onClick = preventMultipleClick {
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
                    modifier = Modifier.padding(padding),
                ) {
                    composable<BottomTab.Search> {
                        SearchScreen(
                            navigateToDetail = { repo ->
                                navController.navigate(DetailRoute(repo = repo))
                            },
                        )
                    }
                    composable<BottomTab.Bookmark> {
                        BookmarkScreen(
                            navigateToDetail = { repo ->
                                navController.navigate(DetailRoute(repo = repo))
                            }
                        )
                    }
                    composable<DetailRoute>(
                        typeMap = mapOf(typeOf<Repo>() to serializableType<Repo>())
                    ) { entry ->
                        val args = entry.toRoute<DetailRoute>()
                        DetailScreen(
                            repo = args.repo,
                            onNavigateUp = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}

inline fun <reified T : Any> serializableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(
        bundle: Bundle,
        key: String,
    ) = bundle.getString(key)?.let { json.decodeFromString<T>(it) }

    override fun parseValue(value: String): T = json.decodeFromString(value)
    override fun serializeAsValue(value: T): String = Uri.encode(json.encodeToString(value))
    override fun put(
        bundle: Bundle,
        key: String,
        value: T,
    ) = bundle.putString(key, json.encodeToString(value))
}

@Serializable
private data class DetailRoute(val repo: Repo)
