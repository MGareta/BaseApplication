package com.core.presentation.ui.components.toolbar.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
internal fun rememberToolbarComponentState(
    initialHeightOffsetLimit: Float = -Float.MAX_VALUE,
    initialHeightOffset: Float = 0F,
    initialContentOffset: Float = 0F
): ToolbarComponentState = rememberSaveable(
    saver = ToolbarComponentState.Saver
) {
    ToolbarComponentState(
        initialHeightOffsetLimit = initialHeightOffsetLimit,
        initialHeightOffset = initialHeightOffset,
        initialContentOffset = initialContentOffset
    )
}

@Stable
class ToolbarComponentState(
    initialHeightOffsetLimit: Float,
    initialHeightOffset: Float,
    initialContentOffset: Float
) {
    private var _heightOffset = mutableFloatStateOf(value = initialHeightOffset)

    var heightOffsetLimit by mutableFloatStateOf(value = initialHeightOffsetLimit)

    var expandedHeight by mutableFloatStateOf(value = 0F)

    var collapsedHeight by mutableFloatStateOf(value = 0F)

    var heightOffset: Float
        get() = _heightOffset.floatValue
        set(newOffset) {
            _heightOffset.floatValue =
                newOffset.coerceIn(minimumValue = heightOffsetLimit, maximumValue = 0f)
        }

    var contentOffset by mutableFloatStateOf(initialContentOffset)

    val collapsedFraction: Float
        get() =
            if (heightOffsetLimit != 0f) {
                heightOffset / heightOffsetLimit
            } else {
                0f
            }

    val overlappedFraction: Float
        get() =
            if (heightOffsetLimit != 0f) {
                1 -
                        ((heightOffsetLimit - contentOffset).coerceIn(
                            minimumValue = heightOffsetLimit,
                            maximumValue = 0f,
                        ) / heightOffsetLimit)
            } else {
                0f
            }

    val collapsedLimit: Lazy<Float>
        get() = lazy {
            1 - (collapsedHeight / expandedHeight)
        }

    companion object {
        val Saver: Saver<ToolbarComponentState, *> =
            listSaver(
                save = { listOf(it.heightOffsetLimit, it.heightOffset, it.contentOffset) },
                restore = {
                    ToolbarComponentState(
                        initialHeightOffsetLimit = it[0],
                        initialHeightOffset = it[1],
                        initialContentOffset = it[2]
                    )
                }
            )
    }
}
