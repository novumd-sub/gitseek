package io.novumd.gitseek.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.novumd.gitseek.R
import io.novumd.gitseek.domain.model.Repo

/**
 * ブックマーク削除確認ダイアログ
 */
@Composable
fun BookmarkDeleteDialog(
    repo: Repo,
    onBookmarkToggle: (Repo, Boolean) -> Unit = { _, _ -> },
    onDissMissRequest: (Boolean) -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = { onDissMissRequest(false) },
        title = { Text(stringResource(R.string.dialog_bookmark_remove_title)) },
        text = { Text(stringResource(R.string.dialog_bookmark_remove_text)) },
        confirmButton = {
            TextButton(
                onClick = preventMultipleClick {
                    onBookmarkToggle(repo, false)
                    onDissMissRequest(false)
                }
            ) { Text(stringResource(R.string.dialog_delete)) }
        },
        dismissButton = {
            TextButton(onClick = preventMultipleClick { onDissMissRequest(false) }) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}
