package com.core.presentation.ui.components.form.fields.input

import android.util.Range
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.core.presentation.ui.components.form.fields.BaseFieldState
import com.core.presentation.ui.components.form.utils.Transform
import com.core.presentation.ui.components.form.validators.Validators
import java.util.Calendar

@Stable
class RangeDateFieldFieldState(
    initial: Range<Calendar>?,
    transform: Transform<Range<Calendar>>,
    validators: List<Validators<Range<Calendar>>>
) : BaseFieldState<Range<Calendar>>(
    initial = initial,
    transform = transform,
    validators = validators,
) {
    override var value: Range<Calendar>? by mutableStateOf(initial)

    override fun change(value: Range<Calendar>) {
        clearError()
        this.value = value
    }

    override fun validate(): Boolean {
        validators.forEach {
            val result = when (it) {
                is Validators.Required -> validateRequired(it.message)
                is Validators.MinDate -> validateMinDate(it.limit, it.message)
                is Validators.MaxDate -> validateMaxDate(it.limit, it.message)
                is Validators.Custom<*> -> validateCustom(it)
                else -> throw Exception(
                    "${it::class.simpleName} validator cannot be called on date state. Did you mean Validators.Custom?",
                )
            }

            if (!result) {
                return false
            }
        }

        return true
    }

    private fun validateRequired(message: String): Boolean {
        val valid = value != null
        if (!valid) setError(message)
        return valid
    }

    private fun validateMinDate(limit: Calendar, message: (Range<Calendar>) -> String): Boolean {
        val valid = value != null && value!!.lower.after(limit)
        if (!valid) setError(message(value!!))
        return valid
    }

    private fun validateMaxDate(limit: Calendar, message: (Range<Calendar>) -> String): Boolean {
        val valid = value != null && value!!.upper.before(limit)
        if (!valid) setError(message(value!!))
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