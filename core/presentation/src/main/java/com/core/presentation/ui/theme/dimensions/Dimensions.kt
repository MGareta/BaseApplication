package com.core.presentation.ui.theme.dimensions

import androidx.compose.runtime.Immutable
import com.core.presentation.ui.theme.dimensions.dp.DpDimensions
import com.core.presentation.ui.theme.dimensions.em.EmDimensions
import com.core.presentation.ui.theme.dimensions.sp.SpDimensions

@Immutable
data class Dimensions(
    val dp: DpDimensions = DpDimensions(),
    val em: EmDimensions = EmDimensions(),
    val sp: SpDimensions = SpDimensions()
)
