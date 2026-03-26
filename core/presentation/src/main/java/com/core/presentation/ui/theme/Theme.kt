package com.core.presentation.ui.theme

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.core.presentation.ui.theme.colors.Colors
import com.core.presentation.ui.theme.dimensions.Dimensions
import com.core.presentation.ui.theme.palette.ColorsPalette
import com.core.presentation.ui.theme.palette.DarkColorsPalette
import com.core.presentation.ui.theme.palette.LightsColorsPalette
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
    val isDark = isSystemInDarkTheme()
    val contrastLevel = rememberSystemContrast()

    val dimensions = remember {
        Dimensions()
    }

    val colors = remember {
        Colors()
    }

    val palette = remember(
        key1 = isDark,
        key2 = contrastLevel
    ) {
        resolvePalette(isDark, contrastLevel)
    }

    val typographies = remember(
        key1 = contrastLevel
    ) {
        Typographies(
            spDimensions = dimensions.sp,
            emDimensions = dimensions.em,
            contrastLevel = contrastLevel
        )
    }

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

private fun resolvePalette(isDark: Boolean, contrast: Float): ColorsPalette = when {
    isDark -> when {
        contrast >= 1.0f -> DarkColorsPalette.HighContrast
        contrast >= 0.5f -> DarkColorsPalette.MediumContrast
        else -> DarkColorsPalette.DefaultContrast
    }
    else -> when {
        contrast >= 1.0f -> LightsColorsPalette.HighContrast
        contrast >= 0.5f -> LightsColorsPalette.MediumContrast
        else -> LightsColorsPalette.DefaultContrast
    }
}

@Composable
private fun rememberSystemContrast(): Float {
    val context = LocalContext.current

    val uiModeManager = remember {
        context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    }

    var contrast by remember {
        mutableFloatStateOf(
            value = if (Build.VERSION.SDK_INT >= 34) {
                uiModeManager.contrast
            } else {
                0f
            }
        )
    }

    if (Build.VERSION.SDK_INT >= 34) {
        DisposableEffect(
            key1 = uiModeManager
        ) {
            val listener = UiModeManager.ContrastChangeListener {
                contrast = it
            }

            uiModeManager.addContrastChangeListener(
                context.mainExecutor,
                listener
            )

            onDispose {
                uiModeManager.removeContrastChangeListener(
                    listener
                )
            }
        }
    }

    return contrast
}
