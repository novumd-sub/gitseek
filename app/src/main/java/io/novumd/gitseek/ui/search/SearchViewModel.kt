package io.novumd.gitseek.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.novumd.gitseek.domain.usecase.SearchReposUseCase
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
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    // 同じ値でもemitして再検索を走らせるためSharedFlowを適用
    private val queryFlow = MutableSharedFlow<String>(extraBufferCapacity = 64)
    private var searchJob: Job? = null

    init {
        observeQuery()
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
                }.collect {
                    // 実際のデータはPagingDataで流れてくるためここでは何もしない
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

                is SearchIntent.ToggleBookmark -> {
                    // TODO: ブックマーク切り替え処理実装
                }
            }
        }
    }
}
