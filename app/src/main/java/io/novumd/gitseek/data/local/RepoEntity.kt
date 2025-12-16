package io.novumd.gitseek.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.novumd.gitseek.domain.model.Repo
import io.novumd.gitseek.domain.model.RepoOwner

/**
 * リポジトリエンティティ
 */
@Entity(tableName = "repos")
data class RepoEntity(
    @PrimaryKey val repoId: Long,
    val repoName: String,
    val description: String?,
    val language: String?,
    val stargazersCount: Int,
    val watchersCount: Int,
    val forksCount: Int,
    val openIssuesCount: Int,
    val ownerName: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val updatedAt: String,
    val isBookmarked: Boolean,
)

fun RepoEntity.toDomainModel() = Repo(
    repoId = repoId,
    repoName = repoName,
    description = description,
    language = language,
    stargazersCount = stargazersCount,
    watchersCount = watchersCount,
    forksCount = forksCount,
    openIssuesCount = openIssuesCount,
    owner = RepoOwner(
        ownerName = ownerName,
        avatarUrl = avatarUrl
    ),
    htmlUrl = htmlUrl,
    updatedAt = updatedAt,
    isBookmarked = isBookmarked,
)
