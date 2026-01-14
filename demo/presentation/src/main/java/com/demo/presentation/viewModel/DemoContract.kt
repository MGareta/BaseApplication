package com.demo.presentation.viewModel

import com.core.presentation.base.viewmodel.IBaseContract

class DemoContract {
    data object UiState : IBaseContract.ViewState
    data object Event : IBaseContract.ViewEvent
    data object Effect : IBaseContract.ViewEffect
}