package com.core.presentation.navigation.model.directions

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.Flow

@Immutable
interface INavigationDirections {
    val directions: Flow<NavigationIntent>
}
