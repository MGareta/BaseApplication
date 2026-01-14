package com.core.presentation.ui.components.form.fields.select.toggle

import com.core.presentation.ui.components.form.fields.select.SelectFieldState
import com.core.presentation.ui.components.form.validators.Validators

class ToggleState(default: Boolean, validators: List<Validators> = emptyList()) :
    SelectFieldState(
        initial = default,
        validators = validators,
    )
