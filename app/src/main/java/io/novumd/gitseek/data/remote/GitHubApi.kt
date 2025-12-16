package io.novumd.gitseek.data.remote

import com.github.michaelbull.result.Result
import io.novumd.gitseek.core.errors.ApiErr
import io.novumd.gitseek.data.model.SearchResponse

interface GitHubApi {

    /**
     * リポジトリ検索API
     */
    suspend fun searchRepositories(
        query: String,
        page: Int,
        perPage: Int,
        sort: String = "stars",
        order: String = "desc",
    ): Result<SearchResponse, ApiErr>
}
