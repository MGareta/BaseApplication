package com.core.presentation.ui.components.cta.icon.model

sealed class IconTypeCta {
    data object Primary : IconTypeCta()

    data object Secondary : IconTypeCta()

    data object Tertiary : IconTypeCta()
}
