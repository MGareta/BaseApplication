package com.demo.presentation.viewModel

import android.util.Log
import com.core.presentation.base.viewmodel.BaseViewModel
import com.core.presentation.navigation.model.arguments.params
import com.demo.presentation.model.DemoScreenNavDestinationTypeMap
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DemoViewModel @Inject constructor(
    val demoViewModelArguments: DemoViewModelArguments
) : BaseViewModel<
        DemoContract.UiState,
        DemoContract.Event,
        DemoContract.Effect
        >() {
    override fun setInitialState(): DemoContract.UiState = DemoContract.UiState

    override fun handleUiEvents(event: DemoContract.Event) {
        TODO("Not yet implemented")
    }

    override fun loadInitData() {
        val params = demoViewModelArguments.params(typeMap = DemoScreenNavDestinationTypeMap().value)
        Log.d("TEST", params.toString())
    }
}
