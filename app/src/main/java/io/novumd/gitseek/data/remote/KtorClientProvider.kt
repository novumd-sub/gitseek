package io.novumd.gitseek.data.remote

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.novumd.gitseek.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

object KtorClientProvider {

    fun create(
        context: Context,
        githubToken: String?,
    ): HttpClient {
        // HTTPキャッシュの設定
        val cacheDir = File(context.cacheDir, "http_cache")
        val cacheSize = 20L * 1024L * 1024L // 20 MiB
        val cache = Cache(cacheDir, cacheSize)

        // ネットワークレスポンスのキャッシュ制御インターセプター
        val networkCacheInterceptor = Interceptor { chain ->
            val request = chain.request()

            // GETリクエストの場合、キャッシュ制御ヘッダーがなければ追加（一貫性担保のため）
            val newRequest = if (request.method.equals("GET", ignoreCase = true)) {
                val hasCacheControl = request.header("Cache-Control") != null
                val builder = request.newBuilder()
                if (!hasCacheControl) {
                    builder.header("Cache-Control", "public, max-age=60")
                }
                builder.build()
            } else {
                request
            }

            // レスポンスにキャッシュ制御ヘッダーやETagがなければ追加（一貫性担保のため）
            val response = chain.proceed(newRequest)
            val hasResponseCache =
                response.header("Cache-Control") != null || response.header("ETag") != null
            if (!hasResponseCache && newRequest.method.equals("GET", ignoreCase = true)) {
                return@Interceptor response.newBuilder()
                    .header("Cache-Control", "public, max-age=60")
                    .build()
            }
            response
        }

        // ロギングインターセプター
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttp = OkHttpClient.Builder()
            .cache(cache)
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            .addNetworkInterceptor(networkCacheInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        return HttpClient(OkHttp) {
            // OkHttpエンジンの設定
            engine {
                preconfigured = okHttp
            }

            // JSONシリアライザーの設定
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }

            // 共通ヘッダーの設定
            install(DefaultRequest) {
                url(BuildConfig.GITHUB_API_URL)
                headers.append("Accept", "application/vnd.github+json")
                headers.append(HttpHeaders.UserAgent, "GitSeek/1.0 (Android)")
                if (!githubToken.isNullOrBlank()) {
                    headers.append(HttpHeaders.Authorization, "token $githubToken")
                }
            }
        }
    }
}
