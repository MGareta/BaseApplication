package com.core.presentation.ui.components.toolbar.state.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.ui.unit.Velocity
import com.core.presentation.ui.components.toolbar.state.ToolbarComponentState
import kotlin.math.abs

internal suspend fun settleComponent(
    state: ToolbarComponentState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?
): Velocity {
    if (state.collapsedFraction < 0.01F || state.collapsedFraction == 1F) {
        return Velocity.Zero
    }

    var remainingVelocity = velocity

    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0F

        AnimationState(
            initialValue = 0F,
            initialVelocity = velocity,
        ).animateDecay(
            animationSpec = flingAnimationSpec
        ) {
            val delta = value - lastValue
            val initialHeightOffset = state.heightOffset

            state.heightOffset = initialHeightOffset + delta

            val consumed = abs(
                x = initialHeightOffset - state.heightOffset
            )

            lastValue = value
            remainingVelocity = this.velocity

            val result = abs(
                x = delta - consumed
            )

            if (result > 0.5f) {
                this.cancelAnimation()
            }
        }
    }

    if (snapAnimationSpec != null) {
        if (state.heightOffset < 0 && state.heightOffset > state.heightOffsetLimit) {
            val limit = 1 - (state.collapsedHeight / state.expandedHeight)

            if (state.collapsedFraction >= limit) {
                AnimationState(
                    initialValue = state.heightOffset
                ).animateTo(
                    targetValue = state.heightOffsetLimit,
                    animationSpec = snapAnimationSpec
                ) {
                    state.heightOffset = value
                }
            }
        }
    }

    return Velocity(
        x = 0f,
        y = remainingVelocity
    )
}
