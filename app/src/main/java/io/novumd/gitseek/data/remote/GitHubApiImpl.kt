package io.novumd.gitseek.data.remote

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.novumd.gitseek.core.errors.ApiErr
import io.novumd.gitseek.data.model.SearchResponse
import timber.log.Timber
import java.net.SocketException
import kotlin.fold
import kotlin.runCatching

class GitHubApiImpl(
    private val client: HttpClient,
) : GitHubApi {

    /**
     * リポジトリ検索API
     */
    override suspend fun searchRepositories(
        query: String,
        page: Int,
        perPage: Int,
        sort: String,
        order: String,
    ): Result<SearchResponse, ApiErr> = callSafety {
        val res = client.get("search/repositories") {
            parameter("q", query)
            parameter("page", page)
            parameter("per_page", perPage)
            parameter("sort", sort)
            parameter("order", order)
        }
        res.body()
    }

    /**
     * Apiで発生する例外をキャッチして、タイプセーフに扱う安全なラッパー関数
     */
    private suspend fun <V> callSafety(block: suspend () -> V): Result<V, ApiErr> = runCatching {
        block()
    }.fold(
        onSuccess = { v -> Ok(v) },
        onFailure = { e ->
            val apiErr = when (e) {
                is SocketException -> ApiErr.Offline
                is ResponseException -> ApiErr.Unexpected
                else -> ApiErr.Unexpected
            }
            Timber.e(e, "API error mapped to $apiErr")
            Err(apiErr)
        }
    )
}
