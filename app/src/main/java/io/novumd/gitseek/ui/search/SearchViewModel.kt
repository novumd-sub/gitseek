package io.novumd.gitseek.ui.search

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.novumd.gitseek.domain.model.Repo
import io.novumd.gitseek.domain.model.RepoOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    init {

        _state.update {
            it.copy(
                query = "Initial Query",
                results = flowOf<PagingData<Repo>>(
                    PagingData.from(
                        List(30) { n ->
                            Repo(
                                repoId = n.toLong(),
                                repoName = "Sample Repo",
                                owner = RepoOwner(
                                    ownerName = "Sample Owner",
                                    avatarUrl = "https://avatars.githubusercontent.com/u/68803158?s=80&v=4"
                                ),
                                description = "This is a sample repository.",
                                language = "Kotlin",
                                stargazersCount = 100,
                                watchersCount = 50,
                                forksCount = 10,
                                openIssuesCount = 5,
                                htmlUrl = "",
                                updatedAt = ""
                            )
                        }
                    )
                ),
            )
        }
    }
}
