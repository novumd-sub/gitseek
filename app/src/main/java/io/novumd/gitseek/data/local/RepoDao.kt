package io.novumd.gitseek.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.novumd.gitseek.core.errors.DbErr
import kotlinx.coroutines.flow.Flow

/**
 * リポジトリDAO
 */
@Dao
interface RepoDao {
    @Query("SELECT * FROM repos WHERE isBookmarked = 1 ORDER BY stargazersCount DESC")
    fun observeBookmarks(): Flow<List<RepoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RepoEntity)

    @Query("DELETE FROM repos WHERE repoId = :repoId")
    suspend fun delete(repoId: Long)

    suspend fun <V> querySafety(block: suspend RepoDao.() -> V): Result<V, DbErr> = runCatching {
        block()
    }.fold(
        onSuccess = { v -> Ok(v) },
        onFailure = { _ -> Err(DbErr.Failure) }
    )
}
