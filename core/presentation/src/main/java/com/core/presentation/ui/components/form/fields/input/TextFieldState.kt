package com.core.presentation.ui.components.form.fields.input

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.core.presentation.ui.components.form.fields.BaseFieldState
import com.core.presentation.ui.components.form.utils.Transform
import com.core.presentation.ui.components.form.utils.isNumeric
import com.core.presentation.ui.components.form.validators.Validators
import java.util.Arrays
import java.util.regex.Pattern

@Stable
class TextFieldState(
    initial: String?,
    transform: Transform<String>,
    validators: List<Validators<String>>
) : BaseFieldState<String>(
    initial = initial,
    transform = transform,
    validators = validators,
) {
    override var value: String? by mutableStateOf(initial)

    override fun change(value: String) {
        clearError()
        this.value = value
    }

    override fun validate(): Boolean {
        validators.forEach {
            val result = when (it) {
                is Validators.Required -> validateRequired(it.message)
                is Validators.MinSize -> validateMinSize(it.limit, it.message)
                is Validators.MaxSize -> validateMaxSize(it.limit, it.message)
                is Validators.MinValue -> validateMinValue(it.limit, it.message)
                is Validators.MaxValue -> validateMaxValue(it.limit, it.message)
                is Validators.Equal -> validateEqual(it.reference, it.message)
                is Validators.Email -> validateEmail(it.message)
                is Validators.Phone -> validatePhone(it.message)
                is Validators.Dni -> validateDni(it.message)
                is Validators.Nie -> validateNie(it.message)
                is Validators.Passport -> validatePassport(it.message)
                is Validators.Regex -> validateRegex(it.regex, it.message)
                is Validators.Custom<*> -> validateCustom(it)
            }

            if (!result) {
                return false
            }
        }

        return true
    }

    private fun validateRequired(message: String): Boolean {
        val valid = value != null && value!!.isNotEmpty()
        if (!valid) setError(message)
        return valid
    }

    private fun validateMinSize(limit: Int, message: (String) -> String): Boolean {
        val valid = value != null && value!!.length >= limit
        if (!valid) setError(message(value!!))
        return valid
    }

    private fun validateMaxSize(limit: Int, message: (String) -> String): Boolean {
        val valid = value != null && value!!.length <= limit
        if (!valid) setError(message(value!!))
        return valid
    }

    private fun validateMinValue(limit: Double, message: (String) -> String): Boolean {
        val valid = value != null && (value!!.isNumeric() && value!!.toDouble() >= limit)
        if (!valid) setError(message(value!!))
        return valid
    }

    private fun validateMaxValue(limit: Double, message: (String) -> String): Boolean {
        val valid = value != null && (value!!.isNumeric() && value!!.toDouble() <= limit)
        if (!valid) setError(message(value!!))
        return valid
    }

    private fun validateEqual(original: String, message: (String) -> String): Boolean {
        val valid = value != null && value == original
        if (!valid) setError(message(value!!))
        return valid
    }

    private fun validateEmail(message: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@])(.+)(\\.)(.+)"
        return validateRegex(emailRegex, message)
    }

    private fun validatePhone(message: String): Boolean {
        val phoneRegex = "^\\d{9}\$"
        return validateRegex(phoneRegex, message)
    }

    private fun validateDni(message: String): Boolean {
        val dniRegex: Pattern = Pattern.compile("[0-9]{8}[A-Z]")
        val control = "TRWAGMYFPDXBNJZSQVHLCKE"
        val invalids = arrayOf("00000000T", "00000001R", "99999999R")

        val valid = value != null && (
            Arrays.binarySearch(invalids, value!!) < 0 &&
                dniRegex.matcher(value!!).matches() &&
                value!![8] == control[Integer.parseInt(value!!.substring(0, 8)) % 23]
            )

        if (!valid) {
            setError(message)
        }

        return valid
    }

    private fun validateNie(message: String): Boolean {
        val dniRegex: Pattern = Pattern.compile("[XYZ][0-9]{7}[A-Z]")
        val control = "TRWAGMYFPDXBNJZSQVHLCKE"
        val invalids = arrayOf("00000000T", "00000001R", "99999999R")

        if (value == null) {
            setError(message)
            return false
        }

        val messageNie = value!!.replaceFirstChar {
            when (it) {
                'X' -> '0'
                'Y' -> '1'
                else -> '2'
            }
        }
        val valid = Arrays.binarySearch(invalids, value!!) < 0 &&
            dniRegex.matcher(value!!).matches() &&
            messageNie[8] == control[Integer.parseInt(messageNie.substring(0, 8)) % 23]

        if (!valid) {
            setError(message)
        }

        return valid
    }

    private fun validatePassport(message: String): Boolean {
        val dniRegex: Pattern = Pattern.compile("[A-Z]{3}[0-9]{6}[A-Z]?")

        val valid = value != null && dniRegex.matcher(value!!).matches()

        if (!valid) {
            setError(message)
        }

        return valid
    }

    private fun validateRegex(regex: String, message: String): Boolean {
        val valid = value != null && regex.toRegex().matches(value!!)
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