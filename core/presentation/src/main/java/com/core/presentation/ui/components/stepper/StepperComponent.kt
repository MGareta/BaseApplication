package com.core.presentation.ui.components.stepper

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.R
import com.core.presentation.ui.components.button.block.BlockButtonComponent
import com.core.presentation.ui.components.button.block.model.BlockButtonSizeType
import com.core.presentation.ui.components.button.block.model.BlockButtonStyleType
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityState
import com.core.presentation.ui.utils.thenOnFalse

@Composable
fun <STEP : Enum<STEP>> StepperComponent(
    modifier: Modifier,
    steps: List<STEP>,
    currentStep: STEP
) {
    val currentStepIndex = remember(steps, currentStep) { steps.indexOf(currentStep) }
    val stepsCount = steps.size

    val stepperContentDescription = stringResource(
        id = R.string.accessibility_step,
        formatArgs = arrayOf(
            currentStepIndex + 1,
            stepsCount
        )
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription = stepperContentDescription
            },
        horizontalArrangement = Arrangement.spacedBy(
            space = LocalDimensions.current.dp.space.space4
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(steps.size) { iteration ->
            val isReached = currentStepIndex >= iteration
            val isCurrent = currentStepIndex == iteration

            Box(
                modifier = Modifier
                    .weight(
                        weight = 1f
                    )
                    .background(
                        color = if (isReached) {
                            LocalPalette.current.backgroundPalette.accent
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    )
                    .height(
                        height = if (isCurrent) {
                            LocalDimensions.current.dp.space.space8
                        } else {
                            LocalDimensions.current.dp.space.space4
                        }
                    )
                    .thenOnFalse(
                        condition = isReached,
                        block = {
                            border(
                                border = BorderStroke(
                                    width = LocalDimensions.current.dp.border.border1,
                                    color = LocalPalette.current.borderPalette.onAccent
                                ),
                                shape = CircleShape
                            )
                        }
                    )
            )
        }
    }
}

@Preview
@Composable
private fun StepperComponentPreview() {
    var currentStep by remember {
        mutableStateOf(
            value = PreviewSteps.STEP_1
        )
    }

    Theme {
        Column(
            modifier = Modifier
                .safeContentPadding()
        ) {
            StepperComponent(
                modifier = Modifier,
                steps = PreviewSteps.entries,
                currentStep = currentStep
            )

            BlockButtonComponent(
                modifier = Modifier,
                blockButtonStyleType = BlockButtonStyleType.Primary,
                blockButtonSizeType = BlockButtonSizeType.Medium,
                text = "Next step",
                leadingIcon = android.R.drawable.star_on,
                trailingIcon = android.R.drawable.star_on,
                enabled = true,
                accessibilityState = AccessibilityState.Click(
                    disabledReason = ""
                ),
                onClick = {
                    val nextStepIndex = currentStep.ordinal + 1

                    if (nextStepIndex < PreviewSteps.entries.size) {
                        currentStep = PreviewSteps.entries[nextStepIndex]
                    }
                }
            )
        }
    }
}

private enum class PreviewSteps {
    STEP_1,
    STEP_2,
    STEP_3
}
