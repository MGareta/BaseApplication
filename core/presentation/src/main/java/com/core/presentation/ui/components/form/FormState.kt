package com.core.presentation.ui.components.form

import com.core.presentation.ui.components.form.fields.BaseFieldState

open class FormState<T : BaseFieldState<*>>(val fields: List<T>) {
    fun validate(): Boolean = fields.map { it.validate() }.all { it }
}
