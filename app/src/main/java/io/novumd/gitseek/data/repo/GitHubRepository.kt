package io.novumd.gitseek.data.repo

import androidx.paging.PagingData
import com.github.michaelbull.result.Result
import io.novumd.gitseek.core.errors.ApiErr
import io.novumd.gitseek.core.errors.DbErr
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

    /**
     * ブックマークのONに切り替えるまたは追加
     */
    suspend fun addBookmark(repo: Repo): Result<Unit, DbErr>

    /**
     * ブックマークを削除
     */
    suspend fun deleteBookmark(repo: Repo): Result<Unit, DbErr>

    /**
     * ブックマーク済みリポジトリの監視用Flowを取得（ドメイン）
     */
    fun observeBookmarks(): Flow<List<Repo>>
}
