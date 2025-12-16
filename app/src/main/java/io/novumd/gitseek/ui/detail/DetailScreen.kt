package io.novumd.gitseek.ui.detail

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import io.novumd.gitseek.R
import io.novumd.gitseek.domain.model.Repo
import io.novumd.gitseek.ui.components.BookmarkDeleteDialog
import io.novumd.gitseek.ui.preview.LanguagePreviews
import io.novumd.gitseek.ui.search.StargazersCount

/**
 * 詳細画面
 */
@Composable
fun DetailScreen(
    repo: Repo,
    onNavigateUp: () -> Unit = {},
    vm: DetailViewModel = hiltViewModel<DetailViewModel>(),
) {
    val pageState by vm.pageState.collectAsState()
    val (showDeleteDialog, setShowDeleteDialog) = remember { mutableStateOf(false) }
    val repoValue = pageState.repo
    val onToggleBookmark = {
        // OFF のときだけダイアログ
        val isNotBookmarked = pageState.repo?.isBookmarked == false
        if (isNotBookmarked) {
            vm.onBookmarkChanged(true)
        } else {
            setShowDeleteDialog(true)
        }
    }

    LaunchedEffect(Unit) {
        vm.load(repo = repo)
    }

    if (showDeleteDialog && repoValue != null) {
        BookmarkDeleteDialog(
            repo = repoValue,
            onBookmarkToggle = { _, _ -> vm.onBookmarkChanged(false) },
            onDissMissRequest = { setShowDeleteDialog(false) }
        )
    }

    DetailScreenContent(
        pageState = pageState,
        onNavigateUp = onNavigateUp,
        onToggleBookmark = onToggleBookmark,
    )
}

@Composable
fun DetailScreenContent(
    pageState: DetailState,
    onNavigateUp: () -> Unit = {},
    onToggleBookmark: () -> Unit = {},
) {
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.clickable { onNavigateUp() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = null,
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.label_back),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Spacer(Modifier.height(16.dp))

        if (pageState.repo != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val url =
                        runCatching { pageState.repo.htmlUrl.toUri() }.getOrNull()
                    if (url != null) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, url))
                    }
                }
            ) {
                Box(Modifier.padding(12.dp)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = pageState.repo.owner.avatarUrl,
                                contentDescription = pageState.repo.owner.ownerName,
                                placeholder = rememberVectorPainter(image = Icons.Filled.Sync),
                                error = rememberVectorPainter(image = Icons.Filled.Error),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(Modifier.width(12.dp))

                            Text(
                                text = pageState.repo.repoName,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(Modifier.width(8.dp))

                            IconButton(onClick = { onToggleBookmark() }) {
                                val icon =
                                    if (pageState.repo.isBookmarked) {
                                        Icons.Filled.Favorite
                                    } else {
                                        Icons.Outlined.FavoriteBorder
                                    }
                                Icon(imageVector = icon, contentDescription = "Bookmark")
                            }
                        }
                        Spacer(Modifier.height(8.dp))

                        val desc = pageState.repo.description
                        if (!desc.isNullOrEmpty()) {
                            Text(desc, style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(Modifier.height(16.dp))

                        StargazersCount(
                            text = pageState.repo.stargazersCount.toString()
                        )

                        Text(
                            text = stringResource(
                                R.string.label_lang,
                                pageState.repo.language ?: "-"
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = stringResource(
                                R.string.label_watchers,
                                pageState.repo.watchersCount
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = stringResource(
                                R.string.label_forks,
                                pageState.repo.forksCount
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = stringResource(
                                R.string.label_issues,
                                pageState.repo.openIssuesCount
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = stringResource(
                                R.string.label_updated,
                                pageState.repo.updatedAt
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.Link,
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
        }
    }
}

@LanguagePreviews
@Composable
private fun DetailScreenContent_Preview() {
    DetailScreenContent(
        pageState = DetailState(
            repo = Repo(
                repoId = 1L,
                repoName = "novumd/gitseek",
                description = "GitHubリポジトリ検索アプリ",
                language = "Kotlin",
                stargazersCount = 100,
                watchersCount = 50,
                forksCount = 20,
                openIssuesCount = 5,
                owner = io.novumd.gitseek.domain.model.RepoOwner(
                    ownerName = "novumd",
                    avatarUrl = "https://avatars.githubusercontent.com/u/12345678?v=4"
                ),
                htmlUrl = "",
                updatedAt = "2024-06-01",
                isBookmarked = true
            ),
        )
    )
}
