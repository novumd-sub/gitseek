package io.novumd.gitseek.data.model

import kotlinx.serialization.Serializable

/**
 * リポジトリ検索APIレスポンス用データ
 */
@Serializable
data class SearchResponse(
    val totalCount: Int,
    val incompleteResults: Boolean,
    val items: List<RepoData>,
)
