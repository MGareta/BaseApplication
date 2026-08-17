package com.core.presentation.ui.components.form.fields.multiselect

import androidx.compose.runtime.Stable
import androidx.compose.runtime.toMutableStateList
import com.core.presentation.ui.components.form.fields.BaseFieldState
import com.core.presentation.ui.components.form.validators.Validators

@Stable
class MultiSelectFieldState<T : Any>(
    initial: List<T>,
    validators: List<Validators<MutableList<T>>>,
) : BaseFieldState<MutableList<T>>(
    initial = initial.toMutableList(),
    transform = null,
    validators = validators
) {
    override var value: MutableList<T>? = super.initial?.toMutableStateList()

    @JvmName(name = "onItemSelected")
    fun change(value: T) {
        clearError()

        if (this.value?.contains(value) == true) {
            this.value?.remove(
                element = value
            )
        } else {
            this.value?.add(
                element = value
            )
        }
    }

    override fun change(value: MutableList<T>) {
        clearError()

        this.value?.clear()

        if (value.isNotEmpty()) {
            this.value?.addAll(
                elements = value
            )
        }
    }

    override fun validate(): Boolean {
        val validations = validators.map {
            when (it) {
                is Validators.MinSize -> validateMinSize(it.limit, it.message)
                is Validators.MaxSize -> validateMaxSize(it.limit, it.message)
                is Validators.Required -> validateRequired(it.message)
                is Validators.Custom<*> -> validateCustom(it)
                else -> throw Exception(
                    "${it::class.simpleName} validator cannot be called on checkbox state. Did you mean Validators.Custom?",
                )
            }
        }
        return validations.all { it }
    }

    private fun validateMinSize(limit: Int, message: (MutableList<T>) -> String): Boolean {
        val valid = value!!.size >= limit
        if (!valid) setError(message(value!!))
        return valid
    }

    private fun validateMaxSize(limit: Int, message: (MutableList<T>) -> String): Boolean {
        val valid = value!!.size <= limit
        if (!valid) setError(message(value!!))
        return valid
    }

    private fun validateRequired(message: String): Boolean {
        val valid = value!!.isNotEmpty()
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
