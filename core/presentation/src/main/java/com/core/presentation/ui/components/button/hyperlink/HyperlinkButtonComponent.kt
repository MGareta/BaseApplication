package com.core.presentation.ui.components.button.hyperlink

import android.content.res.Configuration
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.ui.components.button.hyperlink.model.HyperlinkButtonSizeType
import com.core.presentation.ui.components.button.hyperlink.model.HyperlinkButtonType
import com.core.presentation.ui.components.button.hyperlink.model.rememberHyperlinkButtonSize
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.Theme
import java.net.URL

@Composable
fun HyperlinkButtonComponent(
    modifier: Modifier,
    text: String,
    hyperlinks: Map<String, HyperlinkButtonType>,
    hyperlinkButtonSizeType: HyperlinkButtonSizeType,
    onClick: (HyperlinkButtonType) -> Unit
) {
    val palette = LocalPalette.current

    val hyperlinkButtonSize = rememberHyperlinkButtonSize(
        hyperlinkButtonSizeType = hyperlinkButtonSizeType
    )

    val annotatedString = remember(
        key1 = text,
        key2 = hyperlinks,
        key3 = palette
    ) {
        buildAnnotatedString {
            if (hyperlinks.isEmpty()) {
                append(
                    text = text
                )

                return@buildAnnotatedString
            }

            var currentIndex = 0
            val sortedKeys = hyperlinks.keys
                .sortedByDescending {
                    it.length
                }

            val matches = Regex(
                pattern = sortedKeys
                    .joinToString(
                        separator = "|"
                    ) { key ->
                        "\\b${Regex.escape(key)}\\b"
                    }
            ).findAll(
                input = text
            )

            matches.forEach { match ->
                append(
                    text = text.substring(
                        currentIndex,
                        match.range.first
                    )
                )

                withLink(
                    LinkAnnotation.Clickable(
                        tag = hyperlinks[match.value].toString(),
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = palette.contentPalette.link,
                                textDecoration = TextDecoration.Underline
                            ),
                            focusedStyle = SpanStyle(
                                color = palette.contentPalette.accent,
                                background = palette.backgroundPalette.accentLow
                            ),
                            hoveredStyle = SpanStyle(
                                color = palette.contentPalette.accent,
                                background = palette.backgroundPalette.accentLow
                            ),
                            pressedStyle = SpanStyle(
                                background = palette.backgroundPalette.accent,
                                color = palette.contentPalette.onAccent
                            )
                        ),
                        linkInteractionListener = {
                            hyperlinks[match.value]?.let {
                                onClick(it)
                            }
                        }
                    )
                ) {
                    append(
                        text = match.value
                    )
                }

                currentIndex = match.range.last + 1
            }

            if (currentIndex < text.length) {
                append(
                    text = text.substring(
                        startIndex = currentIndex
                    )
                )
            }
        }
    }

    Text(
        modifier = modifier,
        text = annotatedString,
        style = hyperlinkButtonSize.textStyle,
        color = palette.contentPalette.primary
    )
}

@Preview
@Composable
private fun HyperlinkButtonSmallLightPreview() {
    Theme {
        HyperlinkButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            text = "Link and hyperLink text and another link",
            hyperlinks = buildMap {
                this["Link"] = HyperlinkButtonType.Action {}
                this["text"] = HyperlinkButtonType.Url(url = URL("http://w.com"))
                this["another link"] = HyperlinkButtonType.Url(url = URL("http://w.com"))
            },
            hyperlinkButtonSizeType = HyperlinkButtonSizeType.Small,
            onClick = {

            }
        )
    }
}

@Preview
@Composable
private fun HyperlinkButtonMediumLightPreview() {
    Theme {
        HyperlinkButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            text = "Link and hyperLink text and another link",
            hyperlinks = buildMap {
                this["Link"] = HyperlinkButtonType.Action {}
                this["text"] = HyperlinkButtonType.Url(url = URL("http://w.com"))
                this["another link"] = HyperlinkButtonType.Url(url = URL("http://w.com"))
            },
            hyperlinkButtonSizeType = HyperlinkButtonSizeType.Medium,
            onClick = {

            }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun HyperlinkButtonSmallDarkPreview() {
    Theme {
        HyperlinkButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            text = "Link and hyperLink text and another link",
            hyperlinks = buildMap {
                this["Link"] = HyperlinkButtonType.Action {}
                this["text"] = HyperlinkButtonType.Url(url = URL("http://w.com"))
                this["another link"] = HyperlinkButtonType.Url(url = URL("http://w.com"))
            },
            hyperlinkButtonSizeType = HyperlinkButtonSizeType.Small,
            onClick = {

            }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun HyperlinkButtonMediumDarkPreview() {
    Theme {
        HyperlinkButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            text = "Link and hyperLink text and another link",
            hyperlinks = buildMap {
                this["Link"] = HyperlinkButtonType.Action {}
                this["text"] = HyperlinkButtonType.Url(url = URL("http://w.com"))
                this["another link"] = HyperlinkButtonType.Url(url = URL("http://w.com"))
            },
            hyperlinkButtonSizeType = HyperlinkButtonSizeType.Medium,
            onClick = {

            }
        )
    }
}
