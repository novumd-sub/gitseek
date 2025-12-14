package io.novumd.gitseek.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.michaelbull.result.mapBoth
import io.novumd.gitseek.core.errors.ApiErr
import io.novumd.gitseek.data.model.toDomainModel
import io.novumd.gitseek.data.remote.GitHubApi
import io.novumd.gitseek.domain.model.Repo
import java.net.SocketException

/**
 * リポジトリ検索のPagingSource実装
 */
class RepoPagingSource(
    private val api: GitHubApi,
    private val query: String,
) : PagingSource<Int, Repo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        val page = params.key ?: 1
        val res = api.searchRepositories(
            query = query,
            page = page,
            perPage = 30
        ).mapBoth(
            success = { res ->
                val repos = res.items.map { repoData -> repoData.toDomainModel() }
                val nextKey = if (repos.isEmpty()) null else page + 1
                LoadResult.Page(
                    data = repos,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = nextKey
                )
            },
            failure = { e ->
                // APIエラーを一旦例外に変換してPagingに伝える
                // TODO: より良いエラーハンドリング方法を検討
                val pagingErr: Throwable = when (e) {
                    is ApiErr.Offline -> SocketException()
                    is ApiErr.Unexpected -> IllegalStateException("Unexpected API error")
                }
                LoadResult.Error(pagingErr)
            }
        )
        return res
    }

    override fun getRefreshKey(state: PagingState<Int, Repo>): Int? {
        // 最も近いアンカー位置のページを基にリフレッシュキーを決定
        return state.anchorPosition?.let { anchor ->
            val anchorPage = state.closestPageToPosition(anchor)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
