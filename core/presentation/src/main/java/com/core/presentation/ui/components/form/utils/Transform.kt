package com.core.presentation.ui.components.form.utils

fun interface Transform<T> {
    fun transform(value: T): T
}
