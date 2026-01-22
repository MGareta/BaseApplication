package com.demo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core.presentation.navigation3.manager.INavigationGraphManager
import com.core.presentation.navigation3.model.entry.INavigationEntry
import com.demo.presentation.model.DemoScreenNavDestination
import com.demo.presentation.view.DemoScreen
import com.demo.presentation.viewModel.DemoViewModel
import dagger.Lazy
import javax.inject.Inject

class DemoScreenNavGraphEntry @Inject constructor(
    override val navigator: Lazy<INavigationGraphManager>
) : INavigationEntry<DemoScreenNavDestination, DemoViewModel>() {

    @Composable
    override fun Render(
        params: DemoScreenNavDestination,
        viewModel: DemoViewModel
    ) {
        val state = viewModel.viewState.collectAsStateWithLifecycle().value

        DemoScreen(
            state = state
        ) {
            viewModel.handleUiEvents(
                event = it
            )
        }
    }
}