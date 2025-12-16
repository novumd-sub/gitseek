package io.novumd.gitseek.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.novumd.gitseek.domain.model.Repo
import io.novumd.gitseek.domain.usecase.ObserveBookmarksUseCase
import io.novumd.gitseek.domain.usecase.ToggleBookmarkUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val toggleBookmark: ToggleBookmarkUseCase,
    private val observeBookmarks: ObserveBookmarksUseCase,
) : ViewModel() {

    private val _pageState = MutableStateFlow(DetailState())
    val pageState: StateFlow<DetailState> = _pageState.asStateFlow()

    fun load(repo: Repo) {
        _pageState.update { it.copy(repo = repo) }
        // ブックマーク状態を監視して反映
        viewModelScope.launch {
            observeBookmarks().collectLatest { list ->
                val current = _pageState.value.repo ?: return@collectLatest
                val bookmarked = list.any { it.repoId == current.repoId }
                if (current.isBookmarked != bookmarked) {
                    _pageState.update { st -> st.copy(repo = current.copy(isBookmarked = bookmarked)) }
                }
            }
        }
    }

    fun onBookmarkChanged(newState: Boolean) {
        viewModelScope.launch {
            val currentRepo = _pageState.value.repo
            if (currentRepo != null) {
                toggleBookmark(currentRepo, newState)
            }
        }
    }
}
