package com.core.presentation.ui.components.bottomnavbar.scrollconnection.utils

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.core.presentation.ui.components.bottomnavbar.scrollconnection.BottomNavBarScrollConnection
import com.core.presentation.ui.components.bottomnavbar.scrollconnection.LocalBottomNavBarScrollConnection
import com.core.presentation.ui.components.bottomnavbar.scrollconnection.model.BottomNavBarInlineBehavior
import com.core.presentation.ui.theme.LocalDimensions

@Composable
fun rememberBottomNavBarScrollConnection(
    initialIsInline: Boolean = false,
    scrollThreshold: Dp = LocalDimensions.current.dp.space.space48,
    inlineBehavior: BottomNavBarInlineBehavior = BottomNavBarInlineBehavior.OnScrollDown
): BottomNavBarScrollConnection = with(
    receiver = LocalDensity.current
) {
    val scrollThresholdPx = scrollThreshold.toPx()

    remember(
        key1 = initialIsInline,
        key2 = scrollThreshold.toPx(),
        key3 = inlineBehavior,
    ) {
        BottomNavBarScrollConnection(
            initialIsInline = initialIsInline,
            scrollThresholdPx = scrollThresholdPx,
            inlineBehavior = inlineBehavior
        )
    }
}

@Composable
fun Modifier.bottomNavBarPadding(
    extraPadding: Dp = LocalDimensions.current.dp.space.space16
): Modifier {
    val scrollConnection = LocalBottomNavBarScrollConnection.current

    val animatedBottomPadding by animateDpAsState(
        targetValue = scrollConnection.bottomBarHeight + extraPadding,
        label = "bottomNavBarPaddingAnimation"
    )

    return this
        .padding(bottom = animatedBottomPadding)
}
