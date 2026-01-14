package com.baseapplication

import com.core.presentation.base.activity.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override fun onSessionWarning() {
        TODO("Add navigation to warning screen / dialog -> navController.navigate()")
    }

    override fun onSessionTimeout() {
        super.onSessionTimeout()

        TODO("Add navigation to timeout screen / dialog and login screen -> navController.navigate()")
    }
}
