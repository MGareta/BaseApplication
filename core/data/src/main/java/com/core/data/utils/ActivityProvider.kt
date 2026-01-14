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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.lang.ref.WeakReference

/**
 * `ActivityProvider` is a singleton object responsible for tracking the currently active
 * `FragmentActivity` within an application. It provides access to the current activity
 * and exposes a flow of `FragmentActivity` instances that updates whenever the active
 * activity changes or a new activity is created.
 *
 * This object implements `Application.ActivityLifecycleCallbacks` to monitor activity
 * lifecycle events and maintain an up-to-date reference to the current activity.
 */
object ActivityProvider : Application.ActivityLifecycleCallbacks {
    private lateinit var _applicationContext: Context
    private val _activityFlow = MutableStateFlow(WeakReference<FragmentActivity>(null))

    /**
     * The application context.
     *
     * This property provides access to the global application context. It's safe to hold
     * a long-lived reference to this context, as it will not be recreated or destroyed
     * during the application's lifecycle.  It is often used for operations that are
     * not tied to a specific activity or component, such as accessing system services,
     * shared preferences, or resources that are shared across the entire application.
     *
     * Note: Avoid using this context for UI-related operations, as it is not tied
     * to any specific activity or window. For those operations use the activity
     * context instead.
     */
    val applicationContext: Context
        get() = _applicationContext

    /**
     * A flow emitting the current [FragmentActivity] instances.
     *
     * This flow provides access to the current, active `FragmentActivity` associated with this component.
     * It emits a new value whenever the activity changes or is recreated.
     *
     * The flow is designed to:
     * - Emit distinct activity instances, avoiding redundant emissions for the same activity.
     * - Filter out null activity values.
     * - Only emit activities that are at least in the `INITIALIZED` lifecycle state.
     *
     * This ensures that consumers of the flow receive valid and active activity instances.
     *
     * **Usage:**
     * You can collect this flow to observe and react to changes in the current activity.
     * For example, you might use it to access activity-specific resources or services.
     *
     */
    val activityFlow: Flow<FragmentActivity> =
        _activityFlow
            .asStateFlow()
            .distinctUntilChanged { old, new -> old.get() === new.get() }
            .map { it.get() }
            .filterNotNull()
            .filter { it.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED) }

    /**
     * The currently active [FragmentActivity] instance.
     *
     * This property provides access to the most recently active [FragmentActivity] that
     * has been observed by the underlying activity tracking mechanism. It only returns
     * an activity if its lifecycle state is at least [Lifecycle.State.INITIALIZED],
     * meaning it has been created and is ready to interact with.
     *
     * The value is retrieved from an internal [_activityFlow], which emits a [WeakReference]
     * to the activity. This ensures that the activity can be garbage collected when it is
     * no longer needed.
     *
     * If no activity is currently active or if the active activity's lifecycle state is
     * below [Lifecycle.State.INITIALIZED], this property will return `null`.
     *
     * **Thread Safety:**
     * Accessing this property is generally thread-safe as it reads from a [StateFlow] (represented by _activityFlow).
     * However, keep in mind that the returned [FragmentActivity] instance itself might not be thread-safe,
     * and any interactions with it should respect its thread constraints (typically the main thread).
     *
     * **Note:**
     * - The returned activity might have been destroyed if the reference is cleared from the underlying
     *   [_activityFlow] and a new activity is set.
     * - Usage should be careful to ensure the activity has not been destroyed before use.
     * - This property is designed to obtain the "currently" active activity; if you need a reference to a specific
     * activity
     *   that might not be the currently active one, you should track it separately.
     *
     * @return The currently active [FragmentActivity], or `null` if no activity is active or its
     *         lifecycle is below [Lifecycle.State.INITIALIZED].
     */
    val currentActivity: FragmentActivity?
        get() = _activityFlow.value
            .get()
            ?.takeIf { it.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED) }

    /**
     * Initializes the library with the application context and registers for activity lifecycle callbacks.
     *
     * This function is crucial for the library's operation as it provides the application context
     * needed for various functionalities and enables tracking of activity lifecycle events.
     *
     * **Important:** This method should be called during the application's `onCreate()` method to
     * ensure proper initialization. Calling it later might lead to unexpected behavior or errors.
     *
     * @param application The application instance. This is used to obtain the application context
     *                    and to register the activity lifecycle callbacks. It should be the application instance,
     *                    not an activity or service context.
     * @throws IllegalArgumentException if the provided `application` is null.
     */
    fun init(application: Application) {
        _applicationContext = application
        application.registerActivityLifecycleCallbacks(this)
    }

    /**
     * Called when an activity has been created.
     *
     * This method is part of the ActivityLifecycleCallbacks interface and is invoked
     * after an Activity instance has been fully created. It provides an opportunity
     * to perform actions that depend on the Activity being in a created state.
     *
     * In this specific implementation, it checks if the provided `activity` is an
     * instance of `FragmentActivity`. If it is, it stores a `WeakReference` to this
     * `FragmentActivity` in the `_activityFlow`. Using a `WeakReference` prevents
     * memory leaks by allowing the `FragmentActivity` to be garbage collected even
     * if it's still referenced by `_activityFlow`.
     *
     * @param activity The Activity that has been created.
     * @param bundle If the activity is being re-initialized after previously being
     *               shut down then this Bundle contains the data it most recently
     *               supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        (activity as? FragmentActivity)?.let {
            _activityFlow.value = WeakReference(it)
        }
    }

    /**
     * Called when an activity starts.
     *
     * This method is part of the ActivityLifecycleCallbacks interface. When an activity starts,
     * this method is invoked. It attempts to cast the provided activity to a FragmentActivity.
     * If the cast is successful, a WeakReference to the FragmentActivity is stored in the
     * `_activityFlow`.  Using a WeakReference prevents memory leaks by allowing the
     * FragmentActivity to be garbage collected when no longer needed, even if it's still
     * referenced in the `_activityFlow`.
     *
     * The `_activityFlow` is likely used to track the currently active FragmentActivity in
     * the application, potentially for operations that need to interact with the activity.
     *
     * @param activity The activity that has started.
     */
    override fun onActivityStarted(activity: Activity) {
        (activity as? FragmentActivity)?.let {
            _activityFlow.value = WeakReference(it)
        }
    }

    /**
     * Called when an activity has resumed.
     *
     * This function is typically used in conjunction with ActivityLifecycleCallbacks.
     * It captures a weak reference to the resumed FragmentActivity and updates the
     * internal [_activityFlow] state flow with this reference. This allows
     * observers to track the currently resumed activity without creating strong
     * references that could prevent garbage collection.
     *
     * @param activity The activity that has resumed.
     *                 If this activity is not a FragmentActivity, it will be ignored.
     *
     * @see android.app.Application.ActivityLifecycleCallbacks
     * @see WeakReference
     * @see MutableStateFlow
     */
    override fun onActivityResumed(activity: Activity) {
        (activity as? FragmentActivity)?.let {
            _activityFlow.value = WeakReference(it)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        // Do nothing on ActivityLifecycleCallbacks.onActivityPaused. Not necessary
    }

    override fun onActivityStopped(activity: Activity) {
        // Do nothing on ActivityLifecycleCallbacks.onActivityStopped. Not necessary
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
        // Do nothing on ActivityLifecycleCallbacks.onActivitySaveInstanceState. Not necessary
    }

    override fun onActivityDestroyed(activity: Activity) {
        // Do nothing on ActivityLifecycleCallbacks.onActivitySaveInstanceState. Not necessary
    }
}
