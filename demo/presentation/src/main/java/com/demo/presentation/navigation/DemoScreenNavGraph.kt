package com.demo.presentation.navigation

import com.core.presentation.navigation3.model.graph.type.INavigationGraphBottomNavBar
import com.core.presentation.navigation3.model.graph.type.INavigationGraphLauncher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class DemoScreenNavGraph @Inject constructor() : INavigationGraphLauncher(), INavigationGraphBottomNavBar {

    override val order: Int
        get() = 0

    override val icon: Int
        get() = android.R.drawable.star_on

    override val label: String
        get() = "Test"

    override val notificationCount: StateFlow<Int>
        get() = flowOf(0) as StateFlow<Int>

    override val isFab: Boolean
        get() = false
}