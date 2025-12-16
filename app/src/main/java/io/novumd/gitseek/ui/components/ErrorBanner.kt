package io.novumd.gitseek.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.novumd.gitseek.R
import io.novumd.gitseek.ui.preview.LanguagePreviews
import io.novumd.gitseek.ui.preview.TogglePreviewParameterProvider
import io.novumd.gitseek.ui.theme.GitSeekTheme

/**
 * エラーバナー
 */
@Composable
fun ErrorBanner(
    isOffline: Boolean,
    onRetry: () -> Unit = {},
) {
    val background =
        if (isOffline) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.error
    val contentColor = contentColorFor(backgroundColor = background)
    val title =
        if (isOffline) stringResource(R.string.banner_offline_title) else stringResource(R.string.banner_error_title)

    Column(
        Modifier
            .fillMaxWidth()
            .background(background)
            .padding(8.dp)
    ) {
        Text(
            text = title,
            color = contentColor,
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = stringResource(R.string.msg_common_error),
            color = contentColor
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = preventMultipleClick { onRetry() },
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.label_retry))
        }
    }
}

@LanguagePreviews
@Composable
private fun ErrorBanner_Preview(
    @PreviewParameter(provider = TogglePreviewParameterProvider::class) isOffline: Boolean,
) {
    GitSeekTheme {
        ErrorBanner(
            isOffline = isOffline,
        )
    }
}
