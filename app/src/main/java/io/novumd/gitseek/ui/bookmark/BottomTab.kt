package io.novumd.gitseek.ui.bookmark

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import io.novumd.gitseek.R
import kotlinx.serialization.Serializable

/**
 * Bottom Navigation Barのタブ
 */
sealed interface BottomTab {
    val labelRes: Int

    val icon: ImageVector

    @Serializable
    data class Search(
        @get:StringRes override val labelRes: Int = R.string.tab_search,
    ) : BottomTab {
        override val icon: ImageVector
            get() = Icons.Filled.Search
    }

    @Serializable
    data class Bookmark(
        @get:StringRes override val labelRes: Int = R.string.tab_bookmark,
    ) : BottomTab {
        override val icon: ImageVector
            get() = Icons.Filled.Favorite
    }
}
