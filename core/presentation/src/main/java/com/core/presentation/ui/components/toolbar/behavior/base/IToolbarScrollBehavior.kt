package com.core.presentation.ui.components.toolbar.behavior.base

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.runtime.Stable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import com.core.presentation.ui.components.toolbar.state.ToolbarComponentState

@Stable
interface IToolbarScrollBehavior {

    val state: ToolbarComponentState

    val isPinned: Boolean

    val snapAnimationSpec: AnimationSpec<Float>?

    val flingAnimationSpec: DecayAnimationSpec<Float>?

    val nestedScrollConnection: NestedScrollConnection
}
