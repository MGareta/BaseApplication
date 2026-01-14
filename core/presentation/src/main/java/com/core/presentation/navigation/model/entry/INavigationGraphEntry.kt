package com.core.presentation.navigation.model.entry

import androidx.compose.runtime.Immutable
import androidx.navigation.NavType
import com.core.presentation.base.viewmodel.BaseViewModel
import com.core.presentation.navigation.model.destination.INavigationDestination
import kotlin.reflect.KType

@Immutable
interface INavigationGraphEntry<DESTINATION : INavigationDestination, VIEWMODEL : BaseViewModel<*, *, *>> {
    val params: Map<KType, NavType<*>>
}
