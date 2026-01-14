package com.core.presentation.ui.components.container

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.utils.conditional
import com.core.presentation.ui.utils.isKeyboardOpenAsState

@Composable
fun ContainerComponent(
    modifier: Modifier,
    containerColor: Color = Color.Transparent,
    contentPaddingValues: PaddingValues = PaddingValues(
        all = LocalDimensions.current.dp.dp0,
    ),
    scrollState: ScrollState? = null,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    keyboardContentDocked: (@Composable () -> Unit)? = null,
    keyboardContentUndocked: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier.then(
            other = Modifier
                .imePadding(),
        ),
        contentWindowInsets = WindowInsets(
            top = 0,
            bottom = 0,
        ),
        containerColor = containerColor,
        topBar = topBar,
        bottomBar = {
            if (keyboardContentDocked != null && isKeyboardOpenAsState().value) {
                keyboardContentDocked()
            } else {
                bottomBar()
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = it)
                .conditional(
                    condition = scrollState != null,
                    modifier = {
                        verticalScroll(
                            state = scrollState!!,
                        )
                    },
                ).padding(paddingValues = contentPaddingValues),
        ) {
            content()

            keyboardContentUndocked?.let {
                AnimatedVisibility(
                    modifier = Modifier
                        .fillMaxWidth(),
                    visible = !isKeyboardOpenAsState().value,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it },
                ) {
                    it()
                }
            }
        }
    }
}
