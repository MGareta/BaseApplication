package com.demo.presentation.viewModel

import com.core.presentation.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DemoViewModel @Inject constructor() : BaseViewModel<
        DemoContract.UiState,
        DemoContract.Event,
        DemoContract.Effect
        >() {
    override fun setInitialState(): DemoContract.UiState = DemoContract.UiState

    override fun handleUiEvents(event: DemoContract.Event) {
        TODO("Not yet implemented")
    }

    override fun loadInitData() {
    }
}
