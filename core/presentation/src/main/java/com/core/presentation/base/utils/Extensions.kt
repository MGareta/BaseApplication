package com.core.presentation.base.utils

import com.core.presentation.base.model.UiResult
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <R, T : R> UiResult<T>.getOrElse(onFailure: () -> R): R {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is UiResult.Success -> this.data
        else -> onFailure()
    }
}

fun <T> UiResult<T>.getOrNull(): T? = this.getOrElse { null }
