package com.core.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.core.presentation.ui.theme.colors.Colors
import com.core.presentation.ui.theme.dimensions.Dimensions
import com.core.presentation.ui.theme.palette.ColorsPalette
import com.core.presentation.ui.theme.palette.DarkColorsPalette
import com.core.presentation.ui.theme.palette.LightColorsPalette
import com.core.presentation.ui.theme.typographies.Typographies

val LocalColors = staticCompositionLocalOf<Colors> {
    error("CompositionLocal LocalColors not present")
}

val LocalPalette = staticCompositionLocalOf<ColorsPalette> {
    error("CompositionLocal LocalPalette not present")
}

val LocalDimensions = staticCompositionLocalOf<Dimensions> {
    error("CompositionLocal LocalDimensions not present")
}

val LocalTypographies = staticCompositionLocalOf<Typographies> {
    error("CompositionLocal LocalTypographies not present")
}

@Composable
fun Theme(content: @Composable () -> Unit) {
    val lightColorsPallet: ColorsPalette = LightColorsPalette()
    val darkColorsPalette: ColorsPalette = DarkColorsPalette()
    val palette = if (isSystemInDarkTheme()) {
        darkColorsPalette
    } else {
        lightColorsPallet
    }
    val colors = Colors()
    val dimensions = Dimensions()
    val typographies = Typographies(dimensions = dimensions.sp)

    CompositionLocalProvider(
        LocalPalette provides palette,
        LocalColors provides colors,
        LocalDimensions provides dimensions,
        LocalTypographies provides typographies,
        LocalDensity provides Density(LocalDensity.current.density, 1f),
    ) {
        content()
    }
}
