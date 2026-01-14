package com.core.presentation.ui.components.form.utils

fun String.isNumeric(): Boolean = this.toDoubleOrNull()?.let { true } ?: false
