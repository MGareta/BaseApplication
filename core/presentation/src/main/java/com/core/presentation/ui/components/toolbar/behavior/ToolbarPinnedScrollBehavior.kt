package com.core.presentation.ui.components.toolbar.behavior

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import com.core.presentation.ui.components.toolbar.behavior.base.IToolbarScrollBehavior
import com.core.presentation.ui.components.toolbar.state.ToolbarComponentState

@Stable
class ToolbarPinnedScrollBehavior(
    override val state: ToolbarComponentState,
    val canScroll: () -> Boolean = { true }
) : IToolbarScrollBehavior {

    override val isPinned: Boolean = true

    override val snapAnimationSpec: AnimationSpec<Float>? = null

    override val flingAnimationSpec: DecayAnimationSpec<Float>? = null

    override var nestedScrollConnection = object : NestedScrollConnection {

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (!canScroll()) {
                return Offset.Zero
            }

            if (consumed.y == 0F && available.y > 0F) {
                state.contentOffset = 0F
            } else {
                state.contentOffset += consumed.y
            }

            return Offset.Zero
        }
    }
}
