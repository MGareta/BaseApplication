package com.demo.presentation.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.core.presentation.ui.components.container.ContainerComponent
import com.demo.presentation.viewModel.DemoContract

@Composable
fun DemoScreen(
    state: DemoContract.UiState,
    event: (DemoContract.Event) -> Unit
) {
    ContainerComponent(
        modifier = Modifier,
    ) {
        Text(
            text = "Hello World"
        )
    }
}