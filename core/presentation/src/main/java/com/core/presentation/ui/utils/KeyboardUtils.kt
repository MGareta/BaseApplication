package com.core.presentation.ui.utils

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Composable
fun isKeyboardOpenAsState(): State<Boolean> {
    val keyboardState = remember { mutableStateOf(value = false) }
    val view = LocalView.current

    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            with(Rect()) {
                view.getWindowVisibleDisplayFrame(this)
                val screenHeight = view.rootView.height
                val keypadHeight = screenHeight - this.bottom

                keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                    true
                } else {
                    false
                }
            }
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}
