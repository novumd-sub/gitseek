package io.novumd.gitseek.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.novumd.gitseek.R
import io.novumd.gitseek.domain.model.Repo
import io.novumd.gitseek.domain.model.RepoOwner
import io.novumd.gitseek.ui.components.BookmarkDeleteDialog
import io.novumd.gitseek.ui.components.preventMultipleClick
import io.novumd.gitseek.ui.preview.LanguagePreviews
import io.novumd.gitseek.ui.theme.GitSeekTheme

/**
 * リポジトリ一覧アイテム
 */
@Composable
fun RepoItem(
    repo: Repo?,
    isBookmarked: Boolean,
    onBookmarkToggle: (Repo, Boolean) -> Unit = { _, _ -> },
    onClick: () -> Unit = {},
) {
    // repoが未ロードなら何も表示しない
    if (repo == null) return

    val (showConfirmDelete, setShowConfirmDelete) = remember { mutableStateOf(false) }

    if (showConfirmDelete) {
        BookmarkDeleteDialog(
            repo = repo,
            onBookmarkToggle = onBookmarkToggle,
            onDissMissRequest = { setShowConfirmDelete(false) }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
        onClick = preventMultipleClick(onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = repo.owner.avatarUrl,
                contentDescription = repo.owner.ownerName,
                modifier = Modifier.size(40.dp).clip(CircleShape),
                placeholder = rememberVectorPainter(image = Icons.Filled.Sync),
                error = rememberVectorPainter(image = Icons.Filled.Error),
                contentScale = ContentScale.Crop,
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = repo.repoName,
                    style = MaterialTheme.typography.titleMedium
                )

                StargazersCount(
                    text = repo.stargazersCount.toString()
                )

                Text(
                    text = stringResource(R.string.label_lang, repo.language ?: "-"),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = stringResource(R.string.label_updated, repo.updatedAt),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = preventMultipleClick {
                    val nextState = !isBookmarked
                    if (nextState) {
                        // ON は即時反映
                        onBookmarkToggle(repo, true)
                    } else {
                        // OFF は確認を表示
                        setShowConfirmDelete(true)
                    }
                }
            ) {
                val icon =
                    if (isBookmarked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(R.string.label_bookmark)
                )
            }
        }
    }
}

@Composable
fun StargazersCount(text: String) {
    Row {
        Icon(
            imageVector = Icons.Outlined.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@LanguagePreviews
@Composable
private fun RepoItem_Preview() {
    GitSeekTheme {
        RepoItem(
            repo = Repo(
                repoId = 1,
                repoName = "novumd/gitseek",
                owner = RepoOwner(
                    ownerName = "novumd",
                    avatarUrl = "https://avatars.githubusercontent.com/u/1684035?v=4"
                ),
                language = "Kotlin",
                stargazersCount = 123,
                updatedAt = "2024-06-01",
                htmlUrl = "",
                description = null,
                watchersCount = 0,
                forksCount = 0,
                openIssuesCount = 0,
                isBookmarked = false,
            ),
            isBookmarked = true,
            onBookmarkToggle = { _, _ -> },
            onClick = {}
        )
    }
}
