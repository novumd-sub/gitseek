package io.novumd.gitseek.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.michaelbull.result.Result
import io.novumd.gitseek.core.errors.DbErr
import io.novumd.gitseek.data.local.RepoDao
import io.novumd.gitseek.data.local.toDomainModel
import io.novumd.gitseek.data.remote.GitHubApi
import io.novumd.gitseek.data.remote.paging.RepoPagingSource
import io.novumd.gitseek.domain.model.Repo
import io.novumd.gitseek.domain.model.toDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GitHubRepositoryImpl(
    private val api: GitHubApi,
    private val dao: RepoDao,
) : GitHubRepository {

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

    /**
     * ブックマークのONに切り替える
     */
    override suspend fun addBookmark(repo: Repo): Result<Unit, DbErr> = dao.querySafety {
        upsert(repo.toDataModel().copy(isBookmarked = true))
    }

    /**
     * ブックマークを削除
     */
    override suspend fun deleteBookmark(repo: Repo): Result<Unit, DbErr> = dao.querySafety {
        delete(repo.repoId)
    }

    /**
     * ブックマーク済みリポジトリの監視用Flowを取得
     */
    override fun observeBookmarks(): Flow<List<Repo>> = dao.observeBookmarks().map { repos ->
        repos.map { it.toDomainModel() }
    }
}
