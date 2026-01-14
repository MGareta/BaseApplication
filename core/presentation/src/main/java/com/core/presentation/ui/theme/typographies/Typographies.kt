package com.core.presentation.ui.theme.typographies

import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.core.presentation.R
import com.core.presentation.ui.theme.dimensions.sp.SpDimensions
import com.core.presentation.ui.theme.typographies.high.HighTypographies
import com.core.presentation.ui.theme.typographies.low.LowTypographies
import com.core.presentation.ui.theme.typographies.mid.MidTypographies

class Typographies(dimensions: SpDimensions) {
    val high: HighTypographies = HighTypographies(
        xs = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans_medium,
                    weight = FontWeight.Medium,
                ),
            ),
            fontWeight = FontWeight.Medium,
            fontSize = dimensions.sp20,
            lineHeight = dimensions.sp30,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        xsEmphasis = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans_semibold,
                    weight = FontWeight.SemiBold,
                ),
            ),
            fontWeight = FontWeight.SemiBold,
            fontSize = dimensions.sp20,
            lineHeight = dimensions.sp30,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        s = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans_medium,
                    weight = FontWeight.Medium,
                ),
            ),
            fontWeight = FontWeight.Medium,
            fontSize = dimensions.sp24,
            lineHeight = dimensions.sp32,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        sEmphasis = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans_semibold,
                    weight = FontWeight.SemiBold,
                ),
            ),
            fontWeight = FontWeight.SemiBold,
            fontSize = dimensions.sp24,
            lineHeight = dimensions.sp32,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        m = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans_medium,
                    weight = FontWeight.Medium,
                ),
            ),
            fontWeight = FontWeight.Medium,
            fontSize = dimensions.sp28,
            lineHeight = dimensions.sp36,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        mEmphasis = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans_semibold,
                    weight = FontWeight.SemiBold,
                ),
            ),
            fontWeight = FontWeight.SemiBold,
            fontSize = dimensions.sp28,
            lineHeight = dimensions.sp36,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        l = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans_medium,
                    weight = FontWeight.Medium,
                ),
            ),
            fontWeight = FontWeight.Medium,
            fontSize = dimensions.sp32,
            lineHeight = dimensions.sp40,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        lEmphasis = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans_semibold,
                    weight = FontWeight.SemiBold,
                ),
            ),
            fontWeight = FontWeight.SemiBold,
            fontSize = dimensions.sp32,
            lineHeight = dimensions.sp40,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
    )
    val low: LowTypographies = LowTypographies(
        xs = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans,
                    weight = FontWeight.Normal,
                ),
            ),
            fontWeight = FontWeight.Normal,
            fontSize = dimensions.sp12,
            lineHeight = dimensions.sp16,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        s = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans,
                    weight = FontWeight.Normal,
                ),
            ),
            fontWeight = FontWeight.Normal,
            fontSize = dimensions.sp14,
            lineHeight = dimensions.sp18,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        m = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans,
                    weight = FontWeight.Normal,
                ),
            ),
            fontWeight = FontWeight.Normal,
            fontSize = dimensions.sp16,
            lineHeight = dimensions.sp22,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        l = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans,
                    weight = FontWeight.Normal,
                ),
            ),
            fontWeight = FontWeight.Normal,
            fontSize = dimensions.sp18,
            lineHeight = dimensions.sp24,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
    )
    val mid: MidTypographies = MidTypographies(
        xs = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans,
                    weight = FontWeight.Normal,
                ),
            ),
            fontWeight = FontWeight.Normal,
            fontSize = dimensions.sp12,
            lineHeight = dimensions.sp16,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        xsEmphasis = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans_medium,
                    weight = FontWeight.Medium,
                ),
            ),
            fontWeight = FontWeight.Medium,
            fontSize = dimensions.sp12,
            lineHeight = dimensions.sp16,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        s = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans,
                    weight = FontWeight.Normal,
                ),
            ),
            fontWeight = FontWeight.Normal,
            fontSize = dimensions.sp14,
            lineHeight = dimensions.sp18,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        sEmphasis = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans_medium,
                    weight = FontWeight.Medium,
                ),
            ),
            fontWeight = FontWeight.Medium,
            fontSize = dimensions.sp14,
            lineHeight = dimensions.sp18,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        m = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans,
                    weight = FontWeight.Normal,
                ),
            ),
            fontWeight = FontWeight.Normal,
            fontSize = dimensions.sp16,
            lineHeight = dimensions.sp22,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        mEmphasis = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans_medium,
                    weight = FontWeight.Medium,
                ),
            ),
            fontWeight = FontWeight.Medium,
            fontSize = dimensions.sp16,
            lineHeight = dimensions.sp22,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        l = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans,
                    weight = FontWeight.Normal,
                ),
            ),
            fontWeight = FontWeight.Normal,
            fontSize = dimensions.sp18,
            lineHeight = dimensions.sp24,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
        lEmphasis = TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.work_sans_medium,
                    weight = FontWeight.Medium,
                ),
            ),
            fontWeight = FontWeight.Medium,
            fontSize = dimensions.sp18,
            lineHeight = dimensions.sp24,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            ),
        ),
    )
}
