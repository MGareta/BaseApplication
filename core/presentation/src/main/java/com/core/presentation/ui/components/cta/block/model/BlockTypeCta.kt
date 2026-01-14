package com.core.presentation.ui.components.cta.block.model

sealed class BlockTypeCta {
    data object Primary : BlockTypeCta()

    data object Secondary : BlockTypeCta()

    data object Tertiary : BlockTypeCta()
}
