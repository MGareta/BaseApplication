package com.demo.presentation.model

import com.core.presentation.navigation3.model.destination.INavigationDestination
import kotlinx.serialization.Serializable

@Serializable
data class DemoScreenNavDestination2(
    val name: String
) : INavigationDestination()
