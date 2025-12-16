package io.novumd.gitseek.ui.components

import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

@Composable
fun preventMultipleClick(
    preventMultipleClickTimeInMillis: Long,
    onClick: () -> Unit,
): () -> Unit {
    val processor = remember { MultipleClickProcessor.instance }
    return remember(onClick) { { processor.processEvent(preventMultipleClickTimeInMillis, onClick) } }
}

@Composable
fun preventMultipleClick(onClick: () -> Unit): () -> Unit {
    val processor = remember { MultipleClickProcessor.instance }
    return remember(onClick) { { processor.processEvent(DEFAULT_PREVENT_MULTIPLE_CLICK_TIME_IN_MILLIS, onClick) } }
}

fun Modifier.preventMultipleClickable(
    timeoutMillis: Long = DEFAULT_PREVENT_MULTIPLE_CLICK_TIME_IN_MILLIS,
    onClick: () -> Unit,
): Modifier = composed {
    val handler = preventMultipleClick(timeoutMillis, onClick)
    val interaction = remember { MutableInteractionSource() }
    clickable(
        interactionSource = interaction,
        indication = null,
        onClick = handler
    )
}

private interface MultipleClickProcessor {
    fun processEvent(
        preventMultipleClickTimeInMillis: Long,
        onClick: () -> Unit,
    )

    companion object
}

private val MultipleClickProcessor.Companion.instance: MultipleClickProcessor by lazy {
    MultipleClickProcessorImpl()
}

/**
 * デフォルトの連打防止時間で多重クリックを防止
 */
private class MultipleClickProcessorImpl : MultipleClickProcessor {
    private val now: Long
        get() = SystemClock.uptimeMillis()

    @Volatile
    private var lastClickTime: Long = now

    override fun processEvent(
        preventMultipleClickTimeInMillis: Long,
        onClick: () -> Unit,
    ) {
        if (now >= (lastClickTime + preventMultipleClickTimeInMillis)) {
            onClick()
        }

        lastClickTime = now
    }
}

private const val DEFAULT_PREVENT_MULTIPLE_CLICK_TIME_IN_MILLIS = 300L
