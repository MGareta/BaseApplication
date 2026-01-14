package com.core.presentation

import android.app.Application
import com.core.data.utils.ActivityProvider

open class CoreApp : Application() {
    override fun onCreate() {
        super.onCreate()

        ActivityProvider.init(this)
    }
}
