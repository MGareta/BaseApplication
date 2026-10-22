package com.core.presentation.ui.components.toolbar.behavior

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import com.core.presentation.ui.components.toolbar.behavior.base.IToolbarScrollBehavior
import com.core.presentation.ui.components.toolbar.state.ToolbarComponentState
import com.core.presentation.ui.components.toolbar.state.utils.settleComponent

@Stable
class ToolbarExitUntilCollapsedScrollBehavior(
    override val state: ToolbarComponentState,
    override val snapAnimationSpec: AnimationSpec<Float>?,
    override val flingAnimationSpec: DecayAnimationSpec<Float>?,
    val canScroll: () -> Boolean = { true }
) : IToolbarScrollBehavior {

    override val isPinned: Boolean = false

    override var nestedScrollConnection = object : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (!canScroll() || available.y > 0F) {
                return Offset.Zero
            }

            val prevHeightOffset = state.heightOffset

            state.heightOffset += available.y

            return if (prevHeightOffset != state.heightOffset) {
                available.copy(x = 0F)
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (!canScroll()) {
                return Offset.Zero
            }

            state.contentOffset += consumed.y

            if (available.y < 0F || consumed.y < 0F) {
                val oldHeightOffset = state.heightOffset

                state.heightOffset += consumed.y
                
                return Offset(
                    x = 0F, 
                    y = state.heightOffset - oldHeightOffset
                )
            }

            if (consumed.y == 0F && available.y > 0) {
                state.contentOffset = 0F
            }

            if (available.y > 0F) {
                val oldHeightOffset = state.heightOffset

                state.heightOffset += available.y
                
                return Offset(
                    x = 0F, 
                    y = state.heightOffset - oldHeightOffset
                )
            }
            
            return Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            val superConsumed = super.onPostFling(
                consumed = consumed,
                available = available
            )

            return superConsumed + settleComponent(
                state = state,
                velocity = available.y,
                flingAnimationSpec = flingAnimationSpec,
                snapAnimationSpec = snapAnimationSpec
            )
        }
    }
}
