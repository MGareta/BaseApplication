package com.core.presentation.ui.components.tile.model

import androidx.annotation.DrawableRes

data class TileUiState(
    @param:DrawableRes val icon: Int?,
    val label: String?,
    val paragraph: String?,
    val extraParagraph: String?,
)
