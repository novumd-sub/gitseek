package io.novumd.gitseek.domain.usecase

import androidx.paging.PagingData
import io.novumd.gitseek.data.repo.GitHubRepository
import io.novumd.gitseek.domain.model.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * リポジトリを検索
 */
class SearchReposUsecaseImpl @Inject constructor(
    private val repo: GitHubRepository,
) : SearchReposUseCase {
    override operator fun invoke(query: String): Flow<PagingData<Repo>> = repo.searchRepoPagingData(
        query = query.ifBlank {
            // デフォルトでスター数1万以上の人気リポジトリを表示
            "stars:>10000"
        }
    )
}
