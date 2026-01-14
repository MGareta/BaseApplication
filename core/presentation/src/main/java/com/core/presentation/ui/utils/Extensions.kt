package com.core.presentation.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
fun Dp.dpToRoundPx() = with(LocalDensity.current) { this@dpToRoundPx.roundToPx() }

@Composable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier = if (condition) {
    then(modifier(Modifier))
} else {
    this
}
