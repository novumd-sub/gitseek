package io.novumd.gitseek.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.novumd.gitseek.domain.model.Repo
import io.novumd.gitseek.domain.usecase.ObserveBookmarksUseCase
import io.novumd.gitseek.domain.usecase.ToggleBookmarkUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val toggleBookmark: ToggleBookmarkUseCase,
    observeBookmarks: ObserveBookmarksUseCase,
) : ViewModel() {

    // ブックマーク済みリポジトリ一覧
    val bookmarks: StateFlow<List<Repo>> =
        observeBookmarks()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )

    fun onBookmarkToggle(
        repo: Repo,
        newBookmarkState: Boolean,
    ) {
        viewModelScope.launch {
            // ブックマークのON/OFF切り替え
            toggleBookmark(repo, newBookmarkState)
        }
    }
}
