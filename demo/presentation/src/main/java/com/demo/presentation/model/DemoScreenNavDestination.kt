package com.demo.presentation.model

import androidx.navigation.NavType
import com.core.presentation.navigation.model.arguments.navGraphEntryArgument
import com.core.presentation.navigation.model.destination.INavigationDestination
import kotlinx.serialization.Serializable
import kotlin.collections.mapOf
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@JvmInline
value class DemoScreenNavDestinationTypeMap(
    val value: Map<KType, NavType<DemoScreenNavDestination>> = mapOf(typeOf<DemoScreenNavDestination>() to navGraphEntryArgument<DemoScreenNavDestination>())
)


@Serializable
data class DemoScreenNavDestination(
    val id: String = "1234"
) : INavigationDestination
