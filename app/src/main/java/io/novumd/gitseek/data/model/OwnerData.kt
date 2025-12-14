package io.novumd.gitseek.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * オーナーデータ
 */
@Serializable
data class OwnerData(
    val login: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
)
