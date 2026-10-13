package com.core.presentation.ui.components.bottomnavbar.scrollconnection

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import com.core.presentation.ui.components.bottomnavbar.scrollconnection.model.BottomNavBarInlineBehavior
import com.core.presentation.ui.theme.dimensions.Dimensions

val LocalBottomNavBarScrollConnection = staticCompositionLocalOf<BottomNavBarScrollConnection> {
    error("CompositionLocal LocalBottomNavBarScrollConnection not present")
}

@Stable
class BottomNavBarScrollConnection(
    initialIsInline: Boolean,
    private val scrollThresholdPx: Float,
    private val inlineBehavior: BottomNavBarInlineBehavior
) : NestedScrollConnection {

    var isInline by mutableStateOf(value = initialIsInline)
        private set

    var bottomBarHeight by mutableStateOf(value = Dimensions().dp.space.space0)

    private var accumulatedScroll = 0F

    fun expand() {
        isInline = false
        accumulatedScroll = 0F
    }

    fun inline() {
        isInline = true
        accumulatedScroll = 0F
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (inlineBehavior == BottomNavBarInlineBehavior.Never) {
            return Offset.Zero
        }

        val scrollDelta = consumed.y
        if (scrollDelta == 0F) {
            return Offset.Zero
        }

        if ((accumulatedScroll > 0 && scrollDelta < 0) || (accumulatedScroll < 0 && scrollDelta > 0)) {
            accumulatedScroll = 0F
        }

        accumulatedScroll += scrollDelta

        when (inlineBehavior) {
            BottomNavBarInlineBehavior.OnScrollDown -> {
                if (accumulatedScroll <= -scrollThresholdPx && !isInline) {
                    isInline = true
                    accumulatedScroll = 0F
                } else if (accumulatedScroll >= scrollThresholdPx && isInline) {
                    isInline = false
                    accumulatedScroll = 0F
                }
            }

            BottomNavBarInlineBehavior.OnScrollUp -> {
                if (accumulatedScroll >= scrollThresholdPx && !isInline) {
                    isInline = true
                    accumulatedScroll = 0F
                } else if (accumulatedScroll <= -scrollThresholdPx && isInline) {
                    isInline = false
                    accumulatedScroll = 0F
                }
            }
        }

        return Offset.Zero
    }
}
