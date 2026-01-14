package com.core.presentation.navigation.model.entry.type

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.navigation.NavHostController
import com.core.presentation.base.viewmodel.BaseViewModel
import com.core.presentation.navigation.model.destination.INavigationDestination
import com.core.presentation.navigation.model.entry.INavigationGraphEntry
import com.core.presentation.navigation.model.utils.StableHolder

interface INavigationGraphBottomSheetEntry<
    DESTINATION : INavigationDestination,
    VIEWMODEL : BaseViewModel<*, *, *>,
    > : INavigationGraphEntry<DESTINATION, VIEWMODEL> {
    val secureFlagPolicy: SecureFlagPolicy

    val skipPartiallyExpanded: Boolean

    fun onComposableStarted(viewModel: VIEWMODEL)

    fun onComposableStopped(viewModel: VIEWMODEL)

    @Composable
    fun Render(controller: StableHolder<NavHostController>, viewModel: VIEWMODEL)
}
