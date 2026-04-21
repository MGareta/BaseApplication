package com.core.presentation.ui.components.form.fields.select.checkbox

import android.content.res.Configuration
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.core.presentation.ui.components.button.hyperlink.HyperlinkButtonComponent
import com.core.presentation.ui.components.button.hyperlink.model.HyperlinkButtonSizeType
import com.core.presentation.ui.components.button.hyperlink.model.HyperlinkButtonType
import com.core.presentation.ui.components.form.fields.select.checkbox.model.CheckboxStyleType
import com.core.presentation.ui.components.form.fields.select.checkbox.model.rememberCheckboxStyle
import com.core.presentation.ui.components.form.validators.Validators
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityLabel
import com.core.presentation.ui.utils.AccessibilityStateDescription
import com.core.presentation.ui.utils.thenOnNotNull
import java.net.URL

@Composable
fun CheckboxComponent(
    modifier: Modifier,
    state: CheckboxState,
    checkboxStyleType: CheckboxStyleType,
    accessibilityLabel: AccessibilityLabel,
    accessibilityStateDescription: AccessibilityStateDescription,
    enabled: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }

    val checkboxStyle = rememberCheckboxStyle(
        isSelected = state.getData() ?: false,
        enabled = enabled,
        interactionSource = interactionSource
    )

    Row(
        modifier = modifier
            .background(
                color = checkboxStyle.background,
                shape = RoundedCornerShape(
                    size = LocalDimensions.current.dp.radius.radius4
                )
            )
            .thenOnNotNull(
                condition = checkboxStyle.borderStroke,
                block = {
                    border(
                        border = it,
                        shape = RoundedCornerShape(
                            size = LocalDimensions.current.dp.radius.radius4
                        )
                    )
                }
            )
            .toggleable(
                value = state.getData() ?: false,
                interactionSource = interactionSource,
                indication = null,
                enabled = true,
                role = Role.Checkbox,
                onValueChange = {
                    if (enabled) {
                        state.change(
                            select = it
                        )
                    }
                }
            )
            .semantics(
                mergeDescendants = true
            ) {
                val state = accessibilityStateDescription.selected
                    .takeIf {
                        state.getData() == true
                    }
                    ?: accessibilityStateDescription.notSelected

                val label = accessibilityLabel.disabled
                    .takeIf {
                        !enabled
                    }
                    ?: accessibilityLabel.enabled

                stateDescription = listOfNotNull(
                    state,
                    label
                ).joinToString(". ")

                if (!enabled) {
                    disabled()
                }
            },
        horizontalArrangement = Arrangement.spacedBy(
            space = LocalDimensions.current.dp.space.space16
        ),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            modifier = Modifier
                .semantics {
                    hideFromAccessibility()
                },
            checked = state.getData() ?: false,
            enabled = enabled,
            colors = CheckboxDefaults.colors(
                checkedColor = checkboxStyle.checkboxBackgroundColor,
                uncheckedColor = checkboxStyle.checkboxBorderColor,
                checkmarkColor = checkboxStyle.checkboxMarkColor,
                disabledCheckedColor = Color.Unspecified,
                disabledUncheckedColor = Color.Unspecified,
                disabledIndeterminateColor = Color.Unspecified
            ),
            onCheckedChange = null,
        )

        Column(
            modifier = Modifier
                .weight(
                    weight = 1F,
                    fill = false
                )
                .align(
                    alignment = Alignment.CenterVertically
                )
        ) {
            when (checkboxStyleType) {
                is CheckboxStyleType.Text -> CheckboxTextComponent(
                    checkboxStyleText = checkboxStyleType
                )
                is CheckboxStyleType.Hyperlink -> CheckboxHyperlinkComponent(
                    checkboxStyleHyperlink = checkboxStyleType,
                    onAllLinksClicked = {
                        state.change(
                            select = true
                        )
                    }
                )
                is CheckboxStyleType.Custom -> CheckboxCustomComponent(
                    checkboxStyleCustom = checkboxStyleType
                )
            }
        }
    }
}

@Composable
private fun CheckboxTextComponent(
    checkboxStyleText: CheckboxStyleType.Text
) {
    Text(
        text = checkboxStyleText.text,
        color = LocalPalette.current.contentPalette.primary,
        style = LocalTypographies.current.body.medium
    )

    Text(
        text = checkboxStyleText.description,
        color = LocalPalette.current.contentPalette.secondary,
        style = LocalTypographies.current.body.small
    )
}

@Composable
private fun CheckboxHyperlinkComponent(
    checkboxStyleHyperlink: CheckboxStyleType.Hyperlink,
    onAllLinksClicked: () -> Unit
) {
    val context = LocalContext.current

    val clickedLinks = remember(
        key1 = checkboxStyleHyperlink.hyperlinks
    ) {
        mutableStateMapOf<HyperlinkButtonType, Boolean>().apply {
            checkboxStyleHyperlink
                .hyperlinks
                .values
                .forEach {
                    put(it, false)
                }
        }
    }

    HyperlinkButtonComponent(
        modifier = Modifier,
        text = checkboxStyleHyperlink.text,
        hyperlinks = checkboxStyleHyperlink.hyperlinks,
        hyperlinkButtonSizeType = checkboxStyleHyperlink.hyperlinkButtonSizeType,
        onClick = {
            clickedLinks[it] = true
            if (clickedLinks.values.all { it }) {
                onAllLinksClicked()
            }

            when (it) {
                is HyperlinkButtonType.Action -> it.action()
                is HyperlinkButtonType.Url -> {
                    CustomTabsIntent
                        .Builder()
                        .setShowTitle(true)
                        .build()
                        .run {
                            launchUrl(
                                context,
                                it.url.toString().toUri()
                            )
                        }
                }
            }
        }
    )
}

@Composable
private fun CheckboxCustomComponent(
    checkboxStyleCustom: CheckboxStyleType.Custom
) {
    checkboxStyleCustom.content()
}

@Preview
@Composable
private fun CheckboxComponentLightPreview() {
    Theme {
        Column {
            CheckboxComponent(
                modifier = Modifier
                    .safeContentPadding(),
                state = CheckboxState(
                    default = true,
                    validators = listOf(
                        Validators.Required("Prueba")
                    )
                ),
                checkboxStyleType = CheckboxStyleType.Text(
                    text = "This is a text",
                    description = "This is a description",
                ),
                accessibilityLabel = AccessibilityLabel(
                    enabled = "This is a text. \n This is a description",
                    disabled = "Other mandatory"
                ),
                accessibilityStateDescription = AccessibilityStateDescription(
                    selected = "On",
                    notSelected = "Off"
                ),
                enabled = true
            )

            CheckboxComponent(
                modifier = Modifier
                    .safeContentPadding(),
                state = CheckboxState(
                    default = true,
                    validators = listOf(
                        Validators.Required("Prueba")
                    )
                ),
                checkboxStyleType = CheckboxStyleType.Custom(
                    content = {
                        Text(
                            text = "This is a custom text"
                        )
                    }
                ),
                accessibilityLabel = AccessibilityLabel(
                    enabled = null,
                    disabled = "Other mandatory"
                ),
                accessibilityStateDescription = AccessibilityStateDescription(
                    selected = "On",
                    notSelected = "Off"
                ),
                enabled = true
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun CheckboxComponentDarkPreview() {
    Theme {
        CheckboxComponent(
            modifier = Modifier
                .safeContentPadding(),
            state = CheckboxState(
                default = true,
                validators = listOf(
                    Validators.Required("Prueba")
                )
            ),
            checkboxStyleType = CheckboxStyleType.Hyperlink(
                text = "Link and hyperLink text and another link",
                hyperlinks = buildMap {
                    this["Link"] = HyperlinkButtonType.Action {

                    }
                    this["text"] = HyperlinkButtonType.Url(url = URL("http://w.com"))
                    this["another link"] = HyperlinkButtonType.Url(url = URL("http://w1.com"))
                },
                hyperlinkButtonSizeType = HyperlinkButtonSizeType.Small,
            ),
            accessibilityLabel = AccessibilityLabel(
                enabled = "This is a text. \n This is a description",
                disabled = "Other mandatory"
            ),
            accessibilityStateDescription = AccessibilityStateDescription(
                selected = "On",
                notSelected = "Off"
            ),
            enabled = true
        )
    }
}
