package com.core.presentation.ui.components.tab.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

@Stable
sealed class TabStyleType(
    @param:DrawableRes open val icon: Int?,
    open val text: String?
) {
    data class IconText(
        @param:DrawableRes override val icon: Int,
        override val text: String
    ) : TabStyleType(
        icon = icon,
        text = text
    )

    data class Icon(
        @param:DrawableRes override val icon: Int
    ) : TabStyleType(
        icon = icon,
        text = null
    )

    data class Text(
        override val text: String
    ) : TabStyleType(
        icon = null,
        text = text
    )
}

@Stable
internal data class TabStyle(
    @param:DrawableRes val icon: Int?,
    val text: String?
)


@Composable
internal fun rememberTabStyle(
    tabStyleType: TabStyleType
): TabStyle {
    return remember(
        key1 = tabStyleType
    ) {
        TabStyle(
            icon = tabStyleType.icon,
            text = tabStyleType.text,
        )
    }
}
