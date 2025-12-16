package io.novumd.gitseek.ui.detail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.novumd.gitseek.domain.model.Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor() : ViewModel() {

    private val _pageState = MutableStateFlow(DetailState())
    val pageState: StateFlow<DetailState> = _pageState.asStateFlow()

    fun load(
        repo: Repo?,
        pageSource: DetailNavSource,
    ) {
        when (pageSource) {
            DetailNavSource.Search -> {
                if (repo != null) {
                    _pageState.update { it.copy(repo = repo) }
                }
            }

            DetailNavSource.Bookmark -> {
                // TODO: DB優先の表示は従来通り＋到着後にAPI最新化して差し替え
            }
        }
    }
}
