package io.novumd.gitseek.domain.usecase

import io.novumd.gitseek.data.repo.GitHubRepository
import io.novumd.gitseek.domain.model.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ブックマークされているリポジトリの一覧を監視する
 */
class ObserveBookmarksUseCaseImpl @Inject constructor(
    private val repository: GitHubRepository,
) : ObserveBookmarksUseCase {
    override operator fun invoke(): Flow<List<Repo>> = repository.observeBookmarks()
}
