package com.core.presentation.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core.presentation.coroutines.CoroutinesUseCaseRunner
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<
    UiState : IBaseContract.ViewState,
    Event : IBaseContract.ViewEvent,
    Effect : IBaseContract.ViewEffect,
    > :
    ViewModel(),
    CoroutinesUseCaseRunner {
    final override val useCaseCoroutineScope = viewModelScope

    private val initialState: UiState by lazy { setInitialState() }

    private val _viewState: MutableStateFlow<UiState> = MutableStateFlow(initialState)
    val viewState: StateFlow<UiState> = _viewState.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()

    private val _effect: Channel<Effect> = Channel(capacity = Channel.CONFLATED)
    val effect = _effect.receiveAsFlow()

    abstract fun setInitialState(): UiState

    abstract fun handleUiEvents(event: Event)

    abstract fun loadInitData()

    init {
        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        viewModelScope.launch {
            _event.collect {
                handleUiEvents(it)
            }
        }
    }

    protected fun setState(reducer: UiState.() -> UiState) {
        _viewState.update(reducer)
    }

    protected fun setEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }
}
