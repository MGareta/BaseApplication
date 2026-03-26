package com.core.presentation.ui.theme.dimensions.sp

import androidx.compose.runtime.Immutable
import com.core.presentation.ui.theme.dimensions.sp.body.BodyDimensions
import com.core.presentation.ui.theme.dimensions.sp.display.DisplayDimensions
import com.core.presentation.ui.theme.dimensions.sp.label.LabelDimensions
import com.core.presentation.ui.theme.dimensions.sp.title.TitleDimensions

@Immutable
data class SpDimensions(
    val body: BodyDimensions = BodyDimensions,
    val display: DisplayDimensions = DisplayDimensions,
    val label: LabelDimensions = LabelDimensions,
    val title: TitleDimensions = TitleDimensions
)
