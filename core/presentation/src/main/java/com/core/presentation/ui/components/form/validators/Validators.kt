package com.core.presentation.ui.components.form.validators

sealed interface Validators {
    class MinSize(var limit: Int, var message: String) : Validators

    class MaxSize(var limit: Int, var message: String) : Validators

    class Email(var message: String) : Validators

    class Phone(var message: String) : Validators

    class MinValue(var limit: Double, var message: String) : Validators

    class MaxValue(var limit: Double, var message: String) : Validators

    class Dni(var message: String) : Validators

    class Nie(var message: String) : Validators

    class Passport(var message: String) : Validators

    class Required(var message: String) : Validators

    class Custom(var message: String, var function: (value: Any) -> Boolean) : Validators

    class Regex(var regex: String, var message: String) : Validators

    class Equal(var reference: String, var message: String) : Validators
}
