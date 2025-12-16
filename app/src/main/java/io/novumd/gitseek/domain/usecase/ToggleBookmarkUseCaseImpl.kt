package io.novumd.gitseek.domain.usecase

import com.github.michaelbull.result.Result
import io.novumd.gitseek.core.errors.DbErr
import io.novumd.gitseek.data.repo.GitHubRepository
import io.novumd.gitseek.domain.model.Repo
import javax.inject.Inject

/**
 * ブックマークのON/OFFを切り替える
 */
class ToggleBookmarkUseCaseImpl @Inject constructor(
    private val repository: GitHubRepository,
) : ToggleBookmarkUseCase {
    override suspend operator fun invoke(
        repo: Repo,
        newState: Boolean,
    ): Result<Unit, DbErr> = if (newState) {
        repository.addBookmark(repo)
    } else {
        repository.deleteBookmark(repo)
    }
}
