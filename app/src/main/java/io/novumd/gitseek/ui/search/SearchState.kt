package io.novumd.gitseek.ui.search

import androidx.paging.PagingData
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import io.novumd.gitseek.R
import io.novumd.gitseek.core.errors.ApiErr
import io.novumd.gitseek.domain.model.Repo
import kotlinx.coroutines.flow.Flow

data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val isUnexpectedError: Boolean = false,
    val errorMessage: String? = null,
    val results: Flow<PagingData<Repo>>? = null,
    val bookmarkedIds: Set<Long> = emptySet(),
) {

    /**
     * 結果に応じて state をハンドル
     */
    fun handleResult(
        result: Result<Unit, ApiErr>,
        resolveString: (resId: Int) -> String,
    ): SearchState = result.mapBoth(
        success = {
            copy(
                isOffline = false,
                errorMessage = null
            )
        },
        failure = { err ->
            when (err) {
                is ApiErr.Offline -> {
                    copy(
                        isOffline = true,
                        errorMessage = resolveString(R.string.banner_offline_title)
                    )
                }

                is ApiErr.Unexpected -> {
                    copy(errorMessage = resolveString(R.string.banner_error_title))
                }
            }
        }
    )
}

sealed interface SearchIntent {
    data object Init : SearchIntent
    data class QueryChanged(val value: String) : SearchIntent
    data object EnterPressed : SearchIntent
    data object Retry : SearchIntent
    data object SwipeRefresh : SearchIntent
    data class ToggleBookmark(val repo: Repo, val newState: Boolean) : SearchIntent
}
