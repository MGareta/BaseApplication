package com.core.presentation.ui.utils

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Immutable
sealed interface AccessibilityState {
    data class Click(
        val disabledReason: String
    )

    data class Select(
        val selected: String,
        val notSelected: String
    ) : AccessibilityState

    data class Description(
        val description: String
    )
}


@Composable
fun Modifier.accessibilityFocusTarget(
    key: Any?,
    requester: FocusRequester,
    delayMillis: Long = 50L
): Modifier {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(
        key1 = key
    ) {
        focusManager.clearFocus()

        delay(
            duration = delayMillis.milliseconds
        )

        requester.requestFocus()
    }

    return this
        .focusRequester(requester)
        .focusable()
}
