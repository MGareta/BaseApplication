package com.core.presentation.ui.components.form.fields.select

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.core.presentation.ui.components.form.fields.BaseFieldState
import com.core.presentation.ui.components.form.validators.Validators

class SelectFieldState(
    initial: Boolean,
    validators: List<Validators>
) : BaseFieldState<Boolean>(
    initial = initial,
    validators = validators,
    transform = null
) {
    override var value: Boolean? by mutableStateOf(initial)

    override fun change(value: Boolean) {
        clearError()
        this.value = value
    }

    override fun validate(): Boolean {
        val validations = validators.map {
            when (it) {
                is Validators.Required -> validateRequired(it.message)
                is Validators.Custom<*> -> validateCustom(it)
                else -> throw Exception(
                    "${it::class.simpleName} validator cannot be called on checkbox state. Did you mean Validators.Custom?",
                )
            }
        }
        return validations.all { it }
    }

    private fun validateRequired(message: String): Boolean {
        val valid = value ?: false
        if (!valid) setError(message)
        return valid
    }

    private fun validateCustom(validator: Validators.Custom<*>): Boolean {
        val valid = value?.let {
            validator.validateGeneric(
                value = it
            )
        } ?: false

        if (!valid) setError(validator.message)
        return valid
    }
}
