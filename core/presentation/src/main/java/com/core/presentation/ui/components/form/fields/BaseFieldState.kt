package com.core.presentation.ui.components.form.fields

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.core.presentation.ui.components.form.utils.Transform
import com.core.presentation.ui.components.form.validators.Validators

abstract class BaseFieldState<T>(val initial: T?, val transform: Transform<T>?, val validators: List<Validators>) {
    abstract var value: T?
        internal set

    var errorMessage: String by mutableStateOf(value = "")

    var hasError: Boolean by mutableStateOf(value = false)

    fun setError(error: String) {
        hasError = true
        errorMessage = error
    }

    fun clearError() {
        hasError = false
    }

    abstract fun validate(): Boolean

    open fun getData(): T? = if (transform == null || value == null) value else transform.transform(value!!)
}
