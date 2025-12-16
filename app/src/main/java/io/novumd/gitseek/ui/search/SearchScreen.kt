package io.novumd.gitseek.ui.search

import android.view.ViewTreeObserver
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import io.novumd.gitseek.R
import io.novumd.gitseek.domain.model.Repo
import io.novumd.gitseek.ui.components.ErrorBanner
import io.novumd.gitseek.ui.components.preventMultipleClick
import java.net.SocketException

/**
 * 検索画面
 */
@Composable
fun SearchScreen(
    navigateToDetail: (repo: Repo) -> Unit = { _ -> },
    vm: SearchViewModel = hiltViewModel<SearchViewModel>(),
) {
    val pageState by vm.state.collectAsState()

    SearchScreenContent(
        pageState = pageState,
        dispatchSearchIntent = { intent -> vm.dispatch(intent) },
        navigateToDetail = navigateToDetail,
    )
}

@Composable
private fun SearchScreenContent(
    pageState: SearchState,
    navigateToDetail: (repo: Repo) -> Unit = { _ -> },
    dispatchSearchIntent: (SearchIntent) -> Unit = { _ -> },
) {
    val pagingFlow = pageState.results
    val lazyItems = pagingFlow?.collectAsLazyPagingItems()

    val (keyword, onKeywordChanged) = remember(pageState.query) { mutableStateOf(pageState.query) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // キーボードが閉じたら必ずフォーカスを解除
    KeyboardVisibilityEffect { isVisible ->
        if (!isVisible) {
            focusManager.clearFocus(force = true)
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            // 検索入力バー
            OutlinedTextField(
                value = keyword,
                onValueChange = {
                    onKeywordChanged(it)
                    dispatchSearchIntent(SearchIntent.QueryChanged(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                label = {
                    Text(stringResource(R.string.label_search))
                },
                placeholder = {
                    Text(stringResource(R.string.placeholder_search))
                },
                keyboardActions = KeyboardActions(onSearch = {
                    dispatchSearchIntent(SearchIntent.EnterPressed)
                    keyboardController?.hide()
                }),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search)
            )

            if (lazyItems != null) {
                // ローディングインジケーター
                if (lazyItems.loadState.refresh is LoadState.Loading) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }

                PullToRefreshBox(
                    isRefreshing = false,
                    onRefresh = { dispatchSearchIntent(SearchIntent.SwipeRefresh) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(Modifier.fillMaxSize()) {
                        // エラーバナー
                        lazyItems.also { items ->
                            val error = (items.loadState.refresh as? LoadState.Error)?.error

                            if (error != null) {
                                item {
                                    val (isOffline, message) = when (error) {
                                        is SocketException -> true to stringResource(R.string.banner_offline_title)
                                        else -> false to stringResource(R.string.banner_error_title)
                                    }
                                    ErrorBanner(
                                        isOffline = isOffline,
                                        message = message,
                                    ) {
                                        dispatchSearchIntent(SearchIntent.Retry)
                                    }
                                }
                            }
                        }

                        // 検索結果一覧
                        if (lazyItems.itemCount > 0) {
                            items(lazyItems.itemCount) { index ->
                                val repo = lazyItems[index]
                                if (repo != null) {
                                    val isBookmarked = pageState.bookmarkedIds.contains(repo.repoId)
                                    RepoItem(
                                        repo = repo,
                                        isBookmarked = isBookmarked,
                                        onBookmarkToggle = { r, newState ->
                                            dispatchSearchIntent(
                                                SearchIntent.ToggleBookmark(
                                                    r,
                                                    newState
                                                )
                                            )
                                        },
                                        onClick = preventMultipleClick { navigateToDetail(repo) }
                                    )
                                }
                            }

                            if (lazyItems.loadState.append is LoadState.Loading) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator()

                                        Spacer(Modifier.height(8.dp))

                                        Text(text = stringResource(R.string.loading_next_page))
                                    }
                                }
                            }
                        }

                        // 検索結果0件
                        if (lazyItems.itemCount == 0 && lazyItems.loadState.refresh is LoadState.NotLoading) {
                            item {
                                Box(
                                    Modifier.fillParentMaxSize(),
                                    contentAlignment = androidx.compose.ui.Alignment.Center
                                ) {
                                    Text(stringResource(R.string.msg_empty_results))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyboardVisibilityEffect(onChanged: (isVisible: Boolean) -> Unit) {
    val view = LocalView.current
    val viewTreeObserver = view.viewTreeObserver
    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val isKeyboardOpen =
                ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.ime())
            onChanged(isKeyboardOpen ?: true)
        }

        viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
}

// プレビューが表示されない問題があるため一旦コメントアウト
// @LanguagePreviews
// @Composable
// private fun SearchScreen_Preview() {
//     SearchScreenContent(
//         pageState = SearchState(
//             query = "Compose",
//             results = flowOf(),
//             bookmarkedIds = setOf(1, 2, 3)
//         )
//     )
// }
