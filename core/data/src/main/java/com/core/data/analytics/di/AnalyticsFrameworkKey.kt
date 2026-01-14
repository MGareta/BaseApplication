package com.core.data.analytics.di

import com.core.data.analytics.model.AnalyticsFramework
import dagger.MapKey

@MapKey
annotation class AnalyticsFrameworkKey(val value: AnalyticsFramework)
