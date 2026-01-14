package com.core.presentation.ui.components.form.fields.select.checkbox

import com.core.presentation.ui.components.form.fields.select.SelectFieldState
import com.core.presentation.ui.components.form.validators.Validators

class CheckboxState(default: Boolean, validators: List<Validators> = emptyList()) :
    SelectFieldState(
        initial = default,
        validators = validators,
    )
