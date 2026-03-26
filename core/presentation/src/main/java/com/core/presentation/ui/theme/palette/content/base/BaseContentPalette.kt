package com.core.presentation.ui.theme.palette.content.base

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
interface BaseContentPalette {
    val primary: Color
    val secondary: Color
    val link: Color
    val accent: Color
    val disabled: Color
    val onAccent: Color
    val success: Color
    val error: Color
    val warning: Color
    val info: Color
}
