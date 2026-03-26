package com.core.presentation.ui.theme.dimensions.dp

import androidx.compose.runtime.Immutable
import com.core.presentation.ui.theme.dimensions.dp.border.BorderDimensions
import com.core.presentation.ui.theme.dimensions.dp.radius.RadiusDimensions
import com.core.presentation.ui.theme.dimensions.dp.space.SpaceDimensions
import com.core.presentation.ui.theme.dimensions.dp.target.TargetDimensions

@Immutable
data class DpDimensions(
    val border: BorderDimensions = BorderDimensions,
    val radius: RadiusDimensions = RadiusDimensions,
    val space: SpaceDimensions = SpaceDimensions,
    val target: TargetDimensions = TargetDimensions
)
