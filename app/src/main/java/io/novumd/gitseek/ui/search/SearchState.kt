package io.novumd.gitseek.ui.search

import androidx.paging.PagingData
import io.novumd.gitseek.domain.model.Repo
import kotlinx.coroutines.flow.Flow

data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val errorMessage: String? = null,
    val results: Flow<PagingData<Repo>>? = null,
    val bookmarkedIds: Set<Long> = emptySet(),
)

sealed interface SearchIntent {
    data class QueryChanged(val value: String) : SearchIntent
    data object EnterPressed : SearchIntent
    data object Retry : SearchIntent
    data object SwipeRefresh : SearchIntent
    data class ToggleBookmark(val repo: Repo, val newState: Boolean) : SearchIntent
}
