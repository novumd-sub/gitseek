package io.novumd.gitseek.domain.usecase

import com.github.michaelbull.result.Result
import io.novumd.gitseek.core.errors.DbErr
import io.novumd.gitseek.domain.model.Repo

/**
 * ブックマークのON/OFFを切り替える
 */
interface ToggleBookmarkUseCase {
    suspend operator fun invoke(
        repo: Repo,
        newState: Boolean,
    ): Result<Unit, DbErr>
}
