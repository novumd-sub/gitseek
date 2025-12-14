package io.novumd.gitseek.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * リポジトリ検索APIレスポンス用データ
 */
@Serializable
data class SearchResponse(
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("incomplete_results")
    val incompleteResults: Boolean,
    val items: List<RepoData>,
)
