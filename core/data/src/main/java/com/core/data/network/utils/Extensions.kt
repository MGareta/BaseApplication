package com.core.data.network.utils

import com.core.data.network.model.ApiResult
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

suspend fun <R, T> ApiResult<T>.onSuccess(
    execute: suspend (T) -> R
): ApiResult<T> = apply {
    if (this is ApiResult.Success<T>) {
        execute(data)
    }
}

suspend fun <R, T> ApiResult<T>.onNoContent(
    execute: suspend () -> R
): ApiResult<T> = apply {
    if (this is ApiResult.NoContent<T>) {
        execute()
    }
}

suspend fun <R, T> ApiResult<T>.onError(
    execute: suspend (httpCode: Int, response: String?) -> R
): ApiResult<T> = apply {
    if (this is ApiResult.Error<T>) {
        execute(
            httpCode,
            response
        )
    }
}

suspend fun <R, T> ApiResult<T>.onException(
    execute: suspend (exception: Throwable) -> R
): ApiResult<T> = apply {
    if (this is ApiResult.Exception<T>) {
        execute(exception)
    }
}

@OptIn(ExperimentalContracts::class)
suspend fun <R, T> ApiResult<T>.map(
    transform: suspend (value: T) -> ApiResult<R>
): ApiResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is ApiResult.Success -> try {
            transform(data)
        } catch (exception: Throwable) {
            ApiResult.Exception(
                exception = exception,
            )
        }
        is ApiResult.Error -> ApiResult.Error(httpCode = httpCode, response = response)
        is ApiResult.Exception -> ApiResult.Exception(exception = exception)
        is ApiResult.NoContent -> ApiResult.NoContent()
    }
}

@OptIn(ExperimentalContracts::class)
fun <R, T : R> ApiResult<T>.getOrElse(
    onFailure: () -> R
): R {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is ApiResult.Success -> this.data
        else -> onFailure()
    }
}

fun <T> ApiResult<T>.getOrNull(): T? = this.getOrElse { null }

fun <T> ApiResult<T>.getOrThrow(onFailure: () -> Throwable): T = this.getOrElse { throw onFailure() }
