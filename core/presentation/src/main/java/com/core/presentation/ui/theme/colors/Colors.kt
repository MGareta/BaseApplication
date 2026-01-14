package com.core.presentation.ui.theme.colors

import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.colors.blue.BlueColors
import com.core.presentation.ui.theme.colors.gray.GrayColors
import com.core.presentation.ui.theme.colors.green.GreenColors
import com.core.presentation.ui.theme.colors.orange.OrangeColors
import com.core.presentation.ui.theme.colors.red.RedColors
import com.core.presentation.ui.theme.colors.turquoise.TurquoiseColors
import com.core.presentation.ui.theme.colors.yellow.YellowColors

data class Colors(
    val blue: BlueColors = BlueColors,
    val gray: GrayColors = GrayColors,
    val green: GreenColors = GreenColors,
    val orange: OrangeColors = OrangeColors,
    val red: RedColors = RedColors,
    val turquoise: TurquoiseColors = TurquoiseColors,
    val yellow: YellowColors = YellowColors,
    val white: Color = Color.White,
    val black: Color = Color.Black,
)
