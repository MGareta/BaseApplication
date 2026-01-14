package com.core.presentation.navigation.model.graph

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.StateFlow

@Immutable
interface INavigationGraphBottomNavBar {
    val order: Int

    @get:DrawableRes
    val icon: Int

    val label: String

    val notificationCount: StateFlow<Int>

    val isFab: Boolean
}
