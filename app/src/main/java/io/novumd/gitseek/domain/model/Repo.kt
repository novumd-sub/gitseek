package io.novumd.gitseek.domain.model

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
)

/**
 * リポジトリ所有者情報（ドメインモデル）
 */
@Serializable
data class RepoOwner(
    val ownerName: String,
    val avatarUrl: String,
)
