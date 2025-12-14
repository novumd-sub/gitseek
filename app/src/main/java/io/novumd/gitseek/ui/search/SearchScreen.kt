package io.novumd.gitseek.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import io.novumd.gitseek.R
import io.novumd.gitseek.ui.components.ErrorBanner

@Composable
fun SearchScreen(
    vm: SearchViewModel = hiltViewModel<SearchViewModel>(),
    navigateToDetail: (repoId: Long) -> Unit = { _ -> },
) {
    val pageState by vm.state.collectAsState()
    SearchScreenContent(
        pageState = pageState,
        navigateToDetail = navigateToDetail,
    )
}

@Composable
private fun SearchScreenContent(
    pageState: SearchState,
    navigateToDetail: (repoId: Long) -> Unit = { _ -> },
) {
    val pagingFlow = pageState.results
    val lazyItems = pagingFlow?.collectAsLazyPagingItems()

    val (keyword, onKeywordChanged) = remember(pageState.query) { mutableStateOf(pageState.query) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            // 検索入力バー
            OutlinedTextField(
                value = keyword,
                onValueChange = onKeywordChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                singleLine = true,
                label = {
                    Text(stringResource(R.string.label_search))
                },
                placeholder = {
                    Text(stringResource(R.string.placeholder_search))
                },
                keyboardActions = KeyboardActions(onSearch = {}),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search)
            )

            if (lazyItems != null) {
                // ローディングインジケーター
                if (lazyItems.loadState.refresh is LoadState.Loading) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }

                // 検索結果一覧
                LazyColumn(Modifier.fillMaxSize()) {
                    items(lazyItems.itemCount) { index ->
                        val repo = lazyItems[index]
                        if (repo != null) {
                            RepoItem(
                                repo = repo,
                                isBookmarked = pageState.bookmarkedIds.contains(repo.repoId),
                                onBookmarkToggle = { r, newState -> },
                                onClick = { navigateToDetail(repo.repoId) }
                            )
                        }
                    }

                    // エラーバナー
                    lazyItems.also { items ->
                        when {
                            items.loadState.refresh is LoadState.Error -> {
                                item {
                                    ErrorBanner(
                                        isOffline = false,
                                        message = stringResource(R.string.msg_common_error)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // 初期表示（何もない状態）
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(stringResource(R.string.msg_empty_results))
                }
            }
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
