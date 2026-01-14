package com.core.presentation.ui.components.form.fields.multiselect

import androidx.compose.runtime.toMutableStateList
import com.core.presentation.ui.components.form.fields.BaseFieldState
import com.core.presentation.ui.components.form.utils.Transform
import com.core.presentation.ui.components.form.validators.Validators

open class MultiSelectFieldState<T : Any>(
    initial: MutableList<T> = mutableListOf(),
    transform: Transform<MutableList<T>>? = null,
    validators: List<Validators> = listOf(),
) : BaseFieldState<MutableList<T>>(
    initial = initial,
    transform = transform,
    validators = validators,
) {
    override var value: MutableList<T>? = initial.toMutableStateList()

    fun select(selectValue: T) {
        value!!.add(selectValue)
    }

    fun unselect(selectValue: T) {
        value!!.remove(selectValue)
    }

    override fun validate(): Boolean {
        val validations = validators.map {
            when (it) {
                is Validators.MinSize -> validateMin(it.limit, it.message)
                is Validators.MaxSize -> validateMax(it.limit, it.message)
                is Validators.Required -> validateRequired(it.message)
                is Validators.Custom -> validateCustom(it.function, it.message)
                else -> throw Exception(
                    "${it::class.simpleName} validator cannot be called on checkbox state. Did you mean Validators.Custom?",
                )
            }
        }
        return validations.all { it }
    }

    private fun validateMin(limit: Int, message: String): Boolean {
        val valid = value!!.size >= limit
        if (!valid) setError(message)
        return valid
    }

    private fun validateMax(limit: Int, message: String): Boolean {
        val valid = value!!.size <= limit
        if (!valid) setError(message)
        return valid
    }

    private fun validateRequired(message: String): Boolean {
        val valid = value!!.isNotEmpty()
        if (!valid) setError(message)
        return valid
    }

    private fun validateCustom(function: (List<T>) -> Boolean, message: String): Boolean {
        val valid = function(value!!)
        if (!valid) setError(message)
        return valid
    }
}
