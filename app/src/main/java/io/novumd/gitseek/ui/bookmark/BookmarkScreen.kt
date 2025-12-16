package io.novumd.gitseek.ui.bookmark

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.novumd.gitseek.R
import io.novumd.gitseek.domain.model.Repo
import io.novumd.gitseek.domain.model.RepoOwner
import io.novumd.gitseek.ui.preview.LanguagePreviews
import io.novumd.gitseek.ui.search.RepoItem

/**
 * ブックマーク画面
 */
@Composable
fun BookmarkScreen(
    navigateToDetail: (repo: Repo) -> Unit = { _ -> },
    vm: BookmarkViewModel = hiltViewModel<BookmarkViewModel>(),
) {
    val bookmarks by vm.bookmarks.collectAsState()

    BookmarkScreenContent(
        bookmarks = bookmarks,
        onBookmarkToggle = { repo, newState -> vm.onBookmarkToggle(repo, newState) },
        navigateToDetail = navigateToDetail,
    )
}

@Composable
private fun BookmarkScreenContent(
    bookmarks: List<Repo>,
    onBookmarkToggle: (repo: Repo, newState: Boolean) -> Unit = { _, _ -> },
    navigateToDetail: (repo: Repo) -> Unit = { _ -> },
) {
    if (bookmarks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(R.string.label_bookmark_empty))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(bookmarks.size) { idx ->
                val item = bookmarks[idx]
                RepoItem(
                    repo = item,
                    isBookmarked = true,
                    onBookmarkToggle = { repo, newState -> onBookmarkToggle(repo, newState) },
                    onClick = { navigateToDetail(item) }
                )
            }
        }
    }
}

@LanguagePreviews
@Composable
private fun BookmarkScreen_Preview() {
    BookmarkScreenContent(
        bookmarks = List(10) {
            Repo(
                repoId = it.toLong(),
                repoName = "octocat/Hello-World",
                description = "This your first repo!",
                language = "Kotlin",
                stargazersCount = 1500,
                isBookmarked = true,
                owner = RepoOwner(
                    ownerName = "octocat",
                    avatarUrl = ""
                ),
                updatedAt = "2024-06-01",
                htmlUrl = "",
                watchersCount = 100,
                forksCount = 200,
                openIssuesCount = 10,
            )
        }
    )
}
