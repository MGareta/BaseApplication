package com.core.presentation.ui.components.button.hyperlink.model

import java.net.URL

sealed interface HyperlinkButtonType {
    data class Action(val action: () -> Unit) : HyperlinkButtonType
    data class Url(val url: URL) : HyperlinkButtonType
}
