package com.core.data.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import java.lang.ref.WeakReference

object ActivityProvider : Application.ActivityLifecycleCallbacks {
    private lateinit var _applicationContext: Context
    private val _activityFlow = MutableStateFlow(
        value = WeakReference<FragmentActivity>(null)
    )

    val applicationContext: Context
        get() = _applicationContext

    val activityFlow: Flow<FragmentActivity> =
        _activityFlow
            .asStateFlow()
            .distinctUntilChanged { old, new -> old.get() === new.get() }
            .mapNotNull { it.get() }
            .filter { it.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED) }

    val currentActivity: FragmentActivity?
        get() = _activityFlow.value
            .get()
            ?.takeIf { it.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED) }

    fun init(
        application: Application
    ) {
        _applicationContext = application
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        (activity as? FragmentActivity)?.let {
            _activityFlow.value = WeakReference(it)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        (activity as? FragmentActivity)?.let {
            _activityFlow.value = WeakReference(it)
        }
    }

    override fun onActivityResumed(
        activity: Activity
    ) {
        (activity as? FragmentActivity)?.let {
            _activityFlow.value = WeakReference(it)
        }
    }

    override fun onActivityPaused(
        activity: Activity
    ) {
        // Do nothing on ActivityLifecycleCallbacks.onActivityPaused. Not necessary
    }

    override fun onActivityStopped(
        activity: Activity
    ) {
        // Do nothing on ActivityLifecycleCallbacks.onActivityStopped. Not necessary
    }

    override fun onActivitySaveInstanceState(
        activity: Activity,
        bundle: Bundle
    ) {
        // Do nothing on ActivityLifecycleCallbacks.onActivitySaveInstanceState. Not necessary
    }

    override fun onActivityDestroyed(
        activity: Activity
    ) {
        // Do nothing on ActivityLifecycleCallbacks.onActivitySaveInstanceState. Not necessary
    }
}
