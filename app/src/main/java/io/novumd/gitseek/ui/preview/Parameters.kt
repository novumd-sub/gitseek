package io.novumd.gitseek.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

// Common
class TogglePreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(true, false)
}

class TextOrEmptyPreviewParameterProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String> =
        sequenceOf("", "Not Empty Text.")
}

class TextOrEmptyOrLongPreviewParameterProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String> =
        sequenceOf("", "Not Empty Text.", "Long".repeat(30))
}

class TextShortOrLongPreviewParameterProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String> =
        sequenceOf("Short", "Long".repeat(30))
}
