package com.core.presentation.navigation.model.utils.navigator.bottomSheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestinationBuilder
import androidx.navigation.NavDestinationDsl
import androidx.navigation.NavType
import kotlin.reflect.KClass
import kotlin.reflect.KType

@NavDestinationDsl
class BottomSheetNavigatorDestinationBuilder : NavDestinationBuilder<BottomSheetNavigator.Destination> {
    private val composeNavigator: BottomSheetNavigator
    private val content: @Composable (NavBackStackEntry) -> Unit

    var securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit
    var skipPartiallyExpanded: Boolean = true

    constructor(
        navigator: BottomSheetNavigator,
        route: String,
        content: @Composable (NavBackStackEntry) -> Unit,
    ) : super(navigator, route) {
        this.composeNavigator = navigator
        this.content = content
    }

    constructor(
        navigator: BottomSheetNavigator,
        route: KClass<*>,
        typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
        content: @Composable (NavBackStackEntry) -> Unit,
    ) : super(navigator, route, typeMap) {
        this.composeNavigator = navigator
        this.content = content
    }

    override fun instantiateDestination(): BottomSheetNavigator.Destination =
        BottomSheetNavigator.Destination(composeNavigator, content)

    override fun build(): BottomSheetNavigator.Destination = super.build().also { destination ->
        destination.securePolicy = securePolicy
        destination.skipPartiallyExpanded = skipPartiallyExpanded
    }
}
