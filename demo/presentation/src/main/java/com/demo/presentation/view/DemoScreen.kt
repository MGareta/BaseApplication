package com.demo.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.core.presentation.ui.components.bottomnavbar.scrollconnection.LocalBottomNavBarScrollConnection
import com.core.presentation.ui.components.bottomnavbar.scrollconnection.utils.bottomNavBarPadding
import com.demo.presentation.viewModel.DemoContract

@Composable
fun DemoScreen(
    state: DemoContract.UiState,
    event: (DemoContract.Event) -> Unit
) {
    Column(
        modifier = Modifier
            .nestedScroll(
                connection = LocalBottomNavBarScrollConnection.current
            )
            .verticalScroll(
                state = rememberScrollState()
            )
            .windowInsetsPadding(
                insets = NavigationBarDefaults.windowInsets
            )
            .bottomNavBarPadding()
    ) {
        repeat(55) {
            Text(
                text = "Hello World $it"
            )
        }
    }
}
