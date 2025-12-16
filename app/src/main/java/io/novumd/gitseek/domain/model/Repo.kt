package io.novumd.gitseek.domain.model

import io.novumd.gitseek.data.local.RepoEntity
import kotlinx.serialization.Serializable

/**
 * GitHubリポジトリ情報（ドメインモデル）
 */
@Serializable
data class Repo(
    val repoId: Long,
    val repoName: String,
    val description: String?,
    val language: String?,
    val stargazersCount: Int,
    val watchersCount: Int,
    val forksCount: Int,
    val openIssuesCount: Int,
    val owner: RepoOwner,
    val htmlUrl: String,
    val updatedAt: String,
    val isBookmarked: Boolean,
)

/**
 * リポジトリ所有者情報（ドメインモデル）
 */
@Serializable
data class RepoOwner(
    val ownerName: String,
    val avatarUrl: String,
)

fun Repo.toDataModel(): RepoEntity = RepoEntity(
    repoId = repoId,
    repoName = repoName,
    description = description,
    language = language,
    stargazersCount = stargazersCount,
    watchersCount = watchersCount,
    forksCount = forksCount,
    openIssuesCount = openIssuesCount,
    ownerName = owner.ownerName,
    avatarUrl = owner.avatarUrl,
    htmlUrl = htmlUrl,
    updatedAt = updatedAt,
    isBookmarked = isBookmarked,
)
