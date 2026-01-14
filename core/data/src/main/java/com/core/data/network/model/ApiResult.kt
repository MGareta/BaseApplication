package com.core.data.network.model

sealed interface ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>

    data class Error<out T>(val httpCode: Int, val response: String?) : ApiResult<T>

    data class Exception<out T>(val exception: Throwable) : ApiResult<T>

    class NoContent<out T> : ApiResult<T>
}
