package com.core.presentation.ui.components.bottomsheet.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.core.presentation.R
import com.core.presentation.ui.theme.LocalPalette

@Stable
sealed interface BottomSheetStyleType {
    data object Warning : BottomSheetStyleType
    data object Info : BottomSheetStyleType
    data object Success : BottomSheetStyleType
    data object Error : BottomSheetStyleType
}

@Stable
internal data class BottomSheetStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    @param:DrawableRes val icon: Int
)

@Composable
internal fun rememberBottomSheetStyle(
    bottomSheetStyleType: BottomSheetStyleType
): BottomSheetStyle {
    val palette = LocalPalette.current

    return remember(
        key1 = bottomSheetStyleType,
        key2 = palette
    ) {
        when (bottomSheetStyleType) {
            BottomSheetStyleType.Warning -> BottomSheetStyle(
                backgroundColor = palette.backgroundPalette.warningLow,
                contentColor = palette.contentPalette.warning,
                icon = R.drawable.ic_warning
            )
            BottomSheetStyleType.Info -> BottomSheetStyle(
                backgroundColor = palette.backgroundPalette.infoLow,
                contentColor = palette.contentPalette.info,
                icon = R.drawable.ic_info
            )
            BottomSheetStyleType.Success -> BottomSheetStyle(
                backgroundColor = palette.backgroundPalette.successLow,
                contentColor = palette.contentPalette.success,
                icon = R.drawable.ic_success
            )
            BottomSheetStyleType.Error -> BottomSheetStyle(
                backgroundColor = palette.backgroundPalette.errorLow,
                contentColor = palette.contentPalette.error,
                icon = R.drawable.ic_error
            )
        }
    }
}
