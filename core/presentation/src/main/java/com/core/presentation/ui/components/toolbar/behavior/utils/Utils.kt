package com.core.presentation.ui.components.toolbar.behavior.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.runtime.Composable
import com.core.presentation.ui.components.toolbar.behavior.ToolbarEnterAlwaysScrollBehavior
import com.core.presentation.ui.components.toolbar.behavior.ToolbarExitUntilCollapsedScrollBehavior
import com.core.presentation.ui.components.toolbar.behavior.ToolbarPinnedScrollBehavior
import com.core.presentation.ui.components.toolbar.behavior.base.IToolbarScrollBehavior
import com.core.presentation.ui.components.toolbar.state.ToolbarComponentState
import com.core.presentation.ui.components.toolbar.state.rememberToolbarComponentState

@Composable
fun enterAlwaysScrollBehavior(
    state: ToolbarComponentState = rememberToolbarComponentState(),
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = Spring.StiffnessMediumLow),
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay()
): IToolbarScrollBehavior = ToolbarEnterAlwaysScrollBehavior(
    state = state,
    snapAnimationSpec = snapAnimationSpec,
    flingAnimationSpec = flingAnimationSpec,
    canScroll = canScroll
)

@Composable
fun exitUntilCollapsedScrollBehavior(
    state: ToolbarComponentState = rememberToolbarComponentState(),
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = Spring.StiffnessMediumLow),
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay()
): IToolbarScrollBehavior = ToolbarExitUntilCollapsedScrollBehavior(
    state = state,
    snapAnimationSpec = snapAnimationSpec,
    flingAnimationSpec = flingAnimationSpec,
    canScroll = canScroll
)

@Composable
fun pinnedScrollBehavior(
    state: ToolbarComponentState = rememberToolbarComponentState(),
    canScroll: () -> Boolean = { true }
): IToolbarScrollBehavior = ToolbarPinnedScrollBehavior(
    state = state,
    canScroll = canScroll
)
