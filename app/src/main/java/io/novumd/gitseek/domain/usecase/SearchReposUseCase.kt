package io.novumd.gitseek.domain.usecase

import androidx.paging.PagingData
import io.novumd.gitseek.domain.model.Repo
import kotlinx.coroutines.flow.Flow

interface SearchReposUseCase {
    operator fun invoke(query: String): Flow<PagingData<Repo>>
}
