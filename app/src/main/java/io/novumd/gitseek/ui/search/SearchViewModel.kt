package io.novumd.gitseek.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.novumd.gitseek.domain.usecase.ObserveBookmarksUseCase
import io.novumd.gitseek.domain.usecase.SearchReposUseCase
import io.novumd.gitseek.domain.usecase.ToggleBookmarkUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepos: SearchReposUseCase,
    private val observeBookmarks: ObserveBookmarksUseCase,
    private val toggleBookmark: ToggleBookmarkUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    // 同じ値でもemitして再検索を走らせるためSharedFlowを適用
    private val queryFlow = MutableSharedFlow<String>(extraBufferCapacity = 64)
    private var searchJob: Job? = null

    init {
        observeQuery()
        observeBookmarkIds()
        dispatch(SearchIntent.Init)
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeQuery() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            queryFlow
                .debounce(300)
                .flatMapLatest { q ->
                    val results = searchRepos(q)
                    _state.update {
                        it.copy(
                            query = q,
                            isLoading = true,
                            errorMessage = null,
                            results = results
                        )
                    }
                    emptyFlow<Unit>()
                }.collect { /* no-op */ }
        }
    }

    private fun observeBookmarkIds() {
        viewModelScope.launch {
            observeBookmarks()
                .collect { list ->
                    _state.update { st -> st.copy(bookmarkedIds = list.map { it.repoId }.toSet()) }
                }
        }
    }

    fun dispatch(intent: SearchIntent) {
        viewModelScope.launch {
            when (intent) {
                is SearchIntent.QueryChanged,
                -> queryFlow.emit(intent.value)

                is SearchIntent.Init,
                is SearchIntent.EnterPressed,
                is SearchIntent.Retry,
                is SearchIntent.SwipeRefresh,
                -> queryFlow.emit(_state.value.query)

                is SearchIntent.ToggleBookmark,
                -> toggleBookmark(intent.repo, intent.newState)
            }
        }
    }
}
