package com.core.presentation.navigation.model.arguments

import android.annotation.SuppressLint
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.serialization.decodeArguments
import androidx.navigation.serialization.generateNavArguments
import com.core.presentation.navigation.model.destination.INavigationDestination
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import kotlin.reflect.KType

@Immutable
interface IViewModelNavigationArguments<DESTINATION : INavigationDestination> {
    val savedStateHandle: SavedStateHandle
}

@SuppressLint("RestrictedApi")
@OptIn(InternalSerializationApi::class)
inline fun <reified T : INavigationDestination> IViewModelNavigationArguments<T>.params(
    typeMap: Map<KType, NavType<*>>,
): T {
    val map: MutableMap<String, NavType<*>> = mutableMapOf()
    return with(T::class.serializer()) {
        generateNavArguments(typeMap).onEach { map[it.name] = it.argument.type }
        decodeArguments(savedStateHandle, map)
    }
}
