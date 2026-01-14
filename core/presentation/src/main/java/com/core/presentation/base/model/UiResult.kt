package com.core.presentation.base.model

sealed interface UiResult<out T> {
    data class Success<out T>(val data: T) : UiResult<T>

    data class Error<out T>(val message: String) : UiResult<T>

    class Loading<out T> : UiResult<T>

    class NoContent<out T> : UiResult<T>
}
