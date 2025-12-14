package io.novumd.gitseek.ui.preview

import androidx.compose.ui.tooling.preview.Preview

/**
 * 多言語プレビュー用のアノテーション
 */
@Preview(name = "日本語", group = "Language", locale = "ja-rJP")
@Preview(name = "英語", group = "Language", locale = "en-rUS")
@Preview(name = "その他", group = "Language", locale = "ru")
annotation class LanguagePreviews
