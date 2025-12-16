package io.novumd.gitseek.domain.usecase

import io.novumd.gitseek.domain.model.Repo
import kotlinx.coroutines.flow.Flow

/**
 * ブックマークされているリポジトリの一覧を監視する
 */
interface ObserveBookmarksUseCase {
    operator fun invoke(): Flow<List<Repo>>
}
