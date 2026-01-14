package com.core.presentation.ui.components.form.fields.text

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.core.presentation.ui.components.form.fields.BaseFieldState
import com.core.presentation.ui.components.form.utils.Transform
import com.core.presentation.ui.components.form.utils.isNumeric
import com.core.presentation.ui.components.form.validators.Validators
import java.util.Arrays
import java.util.regex.Pattern

open class TextFieldState(
    initial: String? = null,
    transform: Transform<String>? = null,
    validators: List<Validators> = emptyList(),
) : BaseFieldState<String>(
    initial = initial,
    transform = transform,
    validators = validators,
) {
    override var value: String? by mutableStateOf(initial)

    open fun change(update: String?) {
        clearError()
        this.value = update
    }

    override fun validate(): Boolean {
        validators.forEach {
            val result = when (it) {
                is Validators.Email -> validateEmail(it.message)
                is Validators.Phone -> validatePhone(it.message)
                is Validators.Required -> validateRequired(it.message)
                is Validators.MinSize -> validateMinChars(it.limit, it.message)
                is Validators.MaxSize -> validateMaxChars(it.limit, it.message)
                is Validators.Custom -> validateCustom(it.function, it.message)
                is Validators.MinValue -> validateMinValue(it.limit, it.message)
                is Validators.MaxValue -> validateMaxValue(it.limit, it.message)
                is Validators.Dni -> validateDni(it.message)
                is Validators.Nie -> validateNie(it.message)
                is Validators.Passport -> validatePassport(it.message)
                is Validators.Regex -> validateRegex(it.regex, it.message)
                is Validators.Equal -> validateEqual(it.reference, it.message)
            }

            if (!result) {
                return false
            }
        }

        return true
    }

    internal fun validateCustom(function: (String) -> Boolean, message: String): Boolean {
        val valid = value != null && function(value!!)
        if (!valid) setError(message)
        return valid
    }

    internal fun validateEmail(message: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@])(.+)(\\.)(.+)"
        return validateRegex(emailRegex, message)
    }

    internal fun validatePhone(message: String): Boolean {
        val phoneRegex = "^\\d{9}\$"
        return validateRegex(phoneRegex, message)
    }

    internal fun validateRequired(message: String): Boolean {
        val valid = value != null && value!!.isNotEmpty()
        if (!valid) setError(message)
        return valid
    }

    internal fun validateMaxChars(limit: Int, message: String): Boolean {
        val valid = value != null && value!!.length <= limit
        if (!valid) setError(message)
        return valid
    }

    internal fun validateMinChars(limit: Int, message: String): Boolean {
        val valid = value != null && value!!.length >= limit
        if (!valid) setError(message)
        return valid
    }

    internal fun validateMinValue(limit: Double, message: String): Boolean {
        val valid = value != null && (value!!.isNumeric() && value!!.toDouble() >= limit)
        if (!valid) setError(message)
        return valid
    }

    internal fun validateMaxValue(limit: Double, message: String): Boolean {
        val valid = value != null && (value!!.isNumeric() && value!!.toDouble() <= limit)
        if (!valid) setError(message)
        return valid
    }

    internal fun validateDni(message: String): Boolean {
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

    internal fun validateNie(message: String): Boolean {
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

    internal fun validatePassport(message: String): Boolean {
        val dniRegex: Pattern = Pattern.compile("[A-Z]{3}[0-9]{6}[A-Z]?")

        val valid = value != null && dniRegex.matcher(value!!).matches()

        if (!valid) {
            setError(message)
        }

        return valid
    }

    internal fun validateRegex(regex: String, message: String): Boolean {
        val valid = value != null && regex.toRegex().matches(value!!)
        if (!valid) setError(message)
        return valid
    }

    internal fun validateEqual(original: String, message: String): Boolean {
        val valid = value != null && value == original
        if (!valid) setError(message)
        return valid
    }
}
