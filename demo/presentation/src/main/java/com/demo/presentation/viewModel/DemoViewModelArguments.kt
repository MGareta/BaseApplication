package com.demo.presentation.viewModel

import androidx.lifecycle.SavedStateHandle
import com.core.presentation.navigation.model.arguments.IViewModelNavigationArguments
import com.demo.presentation.model.DemoScreenNavDestination
import javax.inject.Inject

class DemoViewModelArguments @Inject constructor(
    override val savedStateHandle: SavedStateHandle
) : IViewModelNavigationArguments<DemoScreenNavDestination>