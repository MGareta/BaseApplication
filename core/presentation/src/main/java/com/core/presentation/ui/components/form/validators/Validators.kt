package com.core.presentation.ui.components.form.validators

import java.util.Calendar

sealed interface Validators<T> {
    class Required<T>(var message: String) : Validators<T>

    class MinSize<T>(var limit: Int, val message: (value: T) -> String) : Validators<T>

    class MaxSize<T>(val limit: Int, val message: (value: T) -> String) : Validators<T>

    class MinValue<T>(var limit: Double, val message: (value: T) -> String) : Validators<T>

    class MaxValue<T>(var limit: Double, val message: (value: T) -> String) : Validators<T>

    class MinDate<T>(var limit: Calendar, val message: (value: T) -> String) : Validators<T>

    class MaxDate<T>(var limit: Calendar, val message: (value: T) -> String) : Validators<T>

    class Equal<T>(var reference: String, val message: (value: T) -> String) : Validators<T>

    class Email<T>(var message: String) : Validators<T>

    class Phone<T>(var message: String) : Validators<T>

    class Dni<T>(var message: String) : Validators<T>

    class Nie<T>(var message: String) : Validators<T>

    class Passport<T>(var message: String) : Validators<T>

    class Regex<T>(var regex: String, var message: String) : Validators<T>

    data class Custom<T: Any>(var message: String, var function: (value: T) -> Boolean) : Validators<T> {
        fun validateGeneric(value: Any?): Boolean {
            @Suppress("UNCHECKED_CAST")
            return (value as? T)?.let {
                function(it)
            } ?: false
        }
    }
}
