package io.novumd.gitseek.ui.detail

/**
 * 詳細画面への遷移元
 */
enum class DetailNavSource {
    Search, // 検索画面→詳細（キャッシュ）
    Bookmark, // ブックマーク画面→詳細（DB）
}
