package io.novumd.gitseek.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.michaelbull.result.Result
import io.novumd.gitseek.core.errors.ApiErr
import io.novumd.gitseek.data.remote.GitHubApi
import io.novumd.gitseek.data.remote.paging.RepoPagingSource
import io.novumd.gitseek.domain.model.Repo
import kotlinx.coroutines.flow.Flow

class GitHubRepositoryImpl(
    private val api: GitHubApi,
) : GitHubRepository {

    /**
     * ヘルスチェックAPIを呼び出す
     */
    override suspend fun ping(): Result<Unit, ApiErr> = api.ping()

    /**
     * リポジトリ検索のページングデータを取得
     */
    override fun searchRepoPagingData(query: String): Flow<PagingData<Repo>> = Pager(
        config = PagingConfig(
            pageSize = 30,
            initialLoadSize = 60,
            prefetchDistance = 10,
        ),
        pagingSourceFactory = { RepoPagingSource(api, query) }
    ).flow
}
