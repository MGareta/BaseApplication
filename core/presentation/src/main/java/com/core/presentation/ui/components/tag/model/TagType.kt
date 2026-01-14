package com.core.presentation.ui.components.tag.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.LocalPalette

sealed class TagType(
    open val label: String,
    @param:DrawableRes open val icon: Int?,
    val containerColor: @Composable () -> Color,
    val contentColor: @Composable () -> Color,
) {
    data class Brand(override val label: String, override val icon: Int?) :
        TagType(
            label = label,
            icon = icon,
            containerColor = { LocalPalette.current.backgroundPalette.accentLow },
            contentColor = { LocalPalette.current.contentPalette.accent },
        )

    data class Neutral(override val label: String, override val icon: Int?) :
        TagType(
            label = label,
            icon = icon,
            containerColor = { LocalPalette.current.backgroundPalette.secondary },
            contentColor = { LocalPalette.current.contentPalette.mid },
        )

    data class Success(override val label: String, override val icon: Int?) :
        TagType(
            label = label,
            icon = icon,
            containerColor = { LocalPalette.current.semanticBackgroundPalette.successLow },
            contentColor = { LocalPalette.current.semanticContentPalette.success },
        )

    data class Warning(override val label: String, override val icon: Int?) :
        TagType(
            label = label,
            icon = icon,
            containerColor = { LocalPalette.current.semanticBackgroundPalette.warningLow },
            contentColor = { LocalPalette.current.semanticContentPalette.warning },
        )

    data class Error(override val label: String, override val icon: Int?) :
        TagType(
            label = label,
            icon = icon,
            containerColor = { LocalPalette.current.semanticBackgroundPalette.dangerLow },
            contentColor = { LocalPalette.current.semanticContentPalette.danger },
        )
}
