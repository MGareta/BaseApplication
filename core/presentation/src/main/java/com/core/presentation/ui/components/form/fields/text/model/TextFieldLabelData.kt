package com.core.presentation.ui.components.form.fields.text.model

import androidx.annotation.DrawableRes

data class TextFieldLabelData(val label: String, val icon: TextFieldLabelIconData?)

data class TextFieldLabelIconData(@param:DrawableRes val icon: Int, val onClick: () -> Unit)
