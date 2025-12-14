package io.novumd.gitseek.data.model

import io.novumd.gitseek.domain.model.Repo
import io.novumd.gitseek.domain.model.RepoOwner
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * リポジトリデータ
 */
@Serializable
data class RepoData(
    val id: Long,
    val name: String,
    @SerialName("full_name")
    val fullName: String,
    val description: String? = null,
    val language: String? = null,
    @SerialName("stargazers_count")
    val stargazersCount: Int = 0,
    @SerialName("watchers_count")
    val watchersCount: Int = 0,
    @SerialName("forks_count")
    val forksCount: Int = 0,
    @SerialName("open_issues_count")
    val openIssuesCount: Int = 0,
    val owner: OwnerData,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("updated_at")
    val updatedAt: String,
)

fun RepoData.toDomainModel(): Repo = Repo(
    repoId = id,
    repoName = fullName,
    description = description,
    language = language,
    stargazersCount = stargazersCount,
    watchersCount = watchersCount,
    forksCount = forksCount,
    openIssuesCount = openIssuesCount,
    owner = RepoOwner(
        ownerName = owner.login,
        avatarUrl = owner.avatarUrl
    ),
    htmlUrl = htmlUrl,
    updatedAt = updatedAt
)
