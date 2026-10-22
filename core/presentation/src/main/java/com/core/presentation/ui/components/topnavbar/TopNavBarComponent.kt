package com.core.presentation.ui.components.topnavbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.R
import com.core.presentation.ui.components.button.icon.IconButtonComponent
import com.core.presentation.ui.components.button.icon.model.IconButtonSizeType
import com.core.presentation.ui.components.button.icon.model.IconButtonStyleType
import com.core.presentation.ui.components.toolbar.behavior.utils.pinnedScrollBehavior
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBarComponent(
    modifier: Modifier = Modifier,
    title: String,
    actions: List<@Composable () -> Unit> = emptyList(),
    onNavigateBack: (() -> Unit)? = null,
    onNavigateClose: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                color = LocalPalette.current.contentPalette.primary,
                style = LocalTypographies.current.body.large
            )
        },
        navigationIcon = {
            onNavigateBack?.let {
                IconButtonComponent(
                    modifier = Modifier,
                    iconButtonStyleType = IconButtonStyleType.Tertiary,
                    iconButtonSizeType = IconButtonSizeType.Small,
                    icon = R.drawable.ic_back,
                    enabled = true,
                    accessibilityState = AccessibilityState.Click(
                        disabledReason = ""
                    ),
                    contentDescription = stringResource(
                        id = R.string.accessibility_Back
                    ),
                    onClick = it
                )
            }
        },
        actions = {
            actions.forEach {
                it.invoke()
            }

            onNavigateClose?.let {
                IconButtonComponent(
                    modifier = Modifier,
                    iconButtonStyleType = IconButtonStyleType.Tertiary,
                    iconButtonSizeType = IconButtonSizeType.Small,
                    icon = R.drawable.ic_close,
                    enabled = true,
                    accessibilityState = AccessibilityState.Click(
                        disabledReason = ""
                    ),
                    contentDescription = stringResource(
                        id = R.string.accessibility_close
                    ),
                    onClick = it
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LocalPalette.current.backgroundPalette.surface,
            scrolledContainerColor = LocalPalette.current.backgroundPalette.surface,
        )
    )
}

@Preview
@Composable
private fun TopNavBarComponentPreview() {
    Theme {
        val scrollBehavior = pinnedScrollBehavior()

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                TopNavBarComponent(
                    modifier = Modifier,
                    title = "Test",
                    actions = emptyList(),
                    onNavigateBack = {  },
                    onNavigateClose = {  }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        paddingValues = it
                    )
                    .fillMaxSize()
                    .nestedScroll(
                        connection = scrollBehavior.nestedScrollConnection
                    )
                    .verticalScroll(
                        state = rememberScrollState()
                    )
            ) {
                repeat(50) {
                    Text("Hello world")
                }
            }
        }
    }
}

