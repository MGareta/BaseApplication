package com.core.presentation.coroutines

import androidx.navigation.NavBackStackEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun Flow<List<NavBackStackEntry>>.zipWithPreviousCount(): Flow<Pair<Int, List<NavBackStackEntry>>> = flow {
    var previous = 0
    collect { value ->
        emit(Pair(previous, value))
        previous = value.count()
    }
}
