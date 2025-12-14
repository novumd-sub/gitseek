package io.novumd.gitseek.data.repo

import androidx.paging.PagingData
import com.github.michaelbull.result.Result
import io.novumd.gitseek.core.errors.ApiErr
import io.novumd.gitseek.domain.model.Repo
import kotlinx.coroutines.flow.Flow

interface GitHubRepository {

    /**
     * ヘルスチェックAPIを呼び出す
     */
    suspend fun ping(): Result<Unit, ApiErr>

    /**
     * リポジトリ検索のページングデータを取得
     */
    fun searchRepoPagingData(query: String): Flow<PagingData<Repo>>
}
