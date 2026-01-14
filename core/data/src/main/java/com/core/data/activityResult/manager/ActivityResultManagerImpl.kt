package com.core.data.activityResult.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import com.core.data.utils.ActivityProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.atomic.AtomicInteger

private const val SAVED_STATE_REGISTRY_KEY = "activityresult_saved_state"
private const val PENDING_RESULT_KEY = "pending"
private const val LAST_INCREMENT_KEY = "key_increment"

class ActivityResultManagerImpl : IActivityResultManager {
    private val keyIncrement = AtomicInteger(0)
    private var pendingResult: String? = null

    /**
     * **How it works:**
     * 1. **Key Calculation:** It first tries to calculate a unique key for the contract using the `calculateKey` function of `ActivityProvider.currentActivity`. If the current activity is null, it returns `null`.
     * 2. **Pending Result:** It sets the `pendingResult` to the simple name of the contract's class. This is likely used for tracking ongoing result requests.
     * 3. **Activity Flow:** It then uses `ActivityProvider.activityFlow`, which is a flow of current activities.
     * 4. **Map Latest:** It uses `mapLatest` to process the latest activity from the flow.
     * 5. **Prepare Saved Data:** If `isLaunched` is false (meaning this is the first time launching for this key/contract), it calls `prepareSavedData` on the current activity.
     * 6. **Suspend Cancellable Coroutine:** It uses a `suspendCancellableCoroutine` to suspend the coroutine until the result is available.
     * 7. **Register Launcher:** Within the coroutine, it registers an `ActivityResultLauncher` with the activity's `activityResultRegistry`, using the calculated `key`.
     *
     * @see ActivityProvider
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun <I, O, C : ActivityResultContract<I, O>> requestResult(contract: C, input: I): O? {
        var (isLaunched, key) = ActivityProvider.currentActivity?.calculateKey(contract)
            ?: return null

        pendingResult = contract.javaClass.simpleName
        return ActivityProvider.activityFlow
            .mapLatest { currentActivity ->
                if (!isLaunched) {
                    currentActivity.prepareSavedData()
                }

                var launcher: ActivityResultLauncher<I>? = null
                try {
                    suspendCancellableCoroutine<O> { continuation ->
                        launcher = currentActivity.activityResultRegistry.register(
                            key,
                            contract,
                        ) { result ->
                            pendingResult = null
                            currentActivity.clearSavedStateData()
                            continuation.resume(result) {
                            }
                        }

                        if (!isLaunched) {
                            launcher!!.launch(input)
                            isLaunched = true
                        }
                    }
                } finally {
                    launcher?.unregister()
                }
            }.first()
    }

    /**
     * Calculates a unique key for an ActivityResultContract.
     *
     * This function determines whether a previously saved result key should be reused or a new one should be generated.
     * It checks the `savedStateRegistry` for a previously stored state using `SAVED_STATE_REGISTRY_KEY`.
     *
     * If a saved state exists and its `PENDING_RESULT_KEY` matches the `simpleName` of the provided `contract`,
     * it means a result is pending and the previously used key (constructed with the `LAST_INCREMENT_KEY` value) should be reused.
     *
     * Otherwise, a new key is generated using a sequentially incremented counter (`keyIncrement`).
     *
     * @param contract The ActivityResultContract for which to calculate the key.
     * @param C The type of the ActivityResultContract.
     * @return A Pair containing:
     *   - A Boolean indicating whether a previously saved key was reused (true) or a new key was generated (false).
     *   - The calculated key as a String. The key will be in the format "result_X", where X is either the last increment from saved state or a new increment.
     *
     * @see ActivityResultContract
     * @see androidx.savedstate.SavedStateRegistry
     * @see SAVED_STATE_REGISTRY_KEY
     * @see PENDING_RESULT_KEY
     * @see LAST_INCREMENT_KEY
     */
    private fun <C : ActivityResultContract<*, *>> ComponentActivity.calculateKey(contract: C): Pair<Boolean, String> {
        fun generateKey(increment: Int) = "result_$increment"

        val savedBundle: Bundle? = savedStateRegistry.consumeRestoredStateForKey(
            SAVED_STATE_REGISTRY_KEY,
        )

        return if (savedBundle != null && contract.javaClass.simpleName == savedBundle.getString(PENDING_RESULT_KEY)) {
            Pair(true, generateKey(savedBundle.getInt(LAST_INCREMENT_KEY)))
        } else {
            Pair(false, generateKey(keyIncrement.getAndIncrement()))
        }
    }

    /**
     * Prepares the saved state data for the [ComponentActivity].
     *
     * This function ensures that a [SavedStateProvider] is registered for the activity with the key [SAVED_STATE_REGISTRY_KEY].
     *
     * It handles the following scenarios:
     * 1. **Already Registered:** If a [SavedStateProvider] is already registered with the key [SAVED_STATE_REGISTRY_KEY], it does nothing and returns.
     * 2. **Initial Registration:** If no provider is registered, it attempts to register one using [registerSavedData].
     * 3. **Duplicate Key Exception:** If [registerSavedData] throws an [IllegalArgumentException] (indicating a duplicate key registration, although it shouldn't occur here after the first check), it clears any potentially corrupted saved state data using [clearSavedStateData] and then retries registering the provider.
     *
     * This robust approach guarantees that the saved state data is correctly set up, even in edge cases like potential data corruption.
     *
     * Note: This function relies on the existence of `SAVED_STATE_REGISTRY_KEY`, `registerSavedData()`, and `clearSavedStateData()` being defined elsewhere within the context of the class.
     *
     * @receiver The [ComponentActivity] instance to prepare the saved data for.
     */
    private fun ComponentActivity.prepareSavedData() {
        if (savedStateRegistry.getSavedStateProvider(SAVED_STATE_REGISTRY_KEY) != null) {
            return
        }

        try {
            registerSavedData()
        } catch (e: IllegalArgumentException) {
            clearSavedStateData()
            registerSavedData()
        }
    }

    /**
     * Registers a SavedStateProvider with the activity's SavedStateRegistry to persist
     * the state of `pendingResult` and the last increment value (`keyIncrement`).
     *
     * This function should be called within the `ComponentActivity` lifecycle, typically
     * during `onCreate`. It ensures that the specified data is saved and can be restored
     * across configuration changes (e.g., screen rotation) or process death.
     *
     * The saved data is stored under the key `SAVED_STATE_REGISTRY_KEY` in the SavedStateRegistry.
     *
     * @receiver The `ComponentActivity` instance where the saved state provider is registered.
     *
     * @param pendingResult  The current pending result to be saved.
     * @param keyIncrement An AtomicInteger representing the current increment.  The previous value will be saved.
     *
     * @see androidx.savedstate.SavedStateRegistry
     * @see androidx.savedstate.SavedStateRegistryOwner
     * @see androidx.activity.ComponentActivity
     */
    private fun ComponentActivity.registerSavedData() {
        savedStateRegistry.registerSavedStateProvider(
            SAVED_STATE_REGISTRY_KEY,
        ) {
            bundleOf(
                PENDING_RESULT_KEY to pendingResult,
                LAST_INCREMENT_KEY to keyIncrement.get() - 1,
            )
        }
    }

    /**
     * Clears the saved state data associated with the `SAVED_STATE_REGISTRY_KEY`.
     *
     * This function performs two primary actions:
     * 1. **Unregisters the saved state provider:** It removes the provider associated with
     *    `SAVED_STATE_REGISTRY_KEY` from the `SavedStateRegistry`. This prevents further saving of data
     *    under this key.
     * 2. **Consumes the restored state:** It deletes any previously saved data associated with
     *    `SAVED_STATE_REGISTRY_KEY` by consuming it. This ensures that the data is no longer available
     *    for restoration in the future.
     *
     * Use this function when you want to completely discard any previously saved state data and
     * prevent it from being restored after configuration changes or process death.
     *
     * Note: After calling this function, any subsequent attempts to retrieve the saved state using
     * `savedStateRegistry.consumeRestoredStateForKey(SAVED_STATE_REGISTRY_KEY)` will return `null`.
     *
     * @receiver ComponentActivity The `ComponentActivity` instance that holds the `SavedStateRegistry`.
     *
     * @see SavedStateRegistry
     * @see SavedStateRegistry.unregisterSavedStateProvider
     * @see SavedStateRegistry.consumeRestoredStateForKey
     * @see SAVED_STATE_REGISTRY_KEY
     */
    private fun ComponentActivity.clearSavedStateData() {
        savedStateRegistry.unregisterSavedStateProvider(
            SAVED_STATE_REGISTRY_KEY,
        )
        // Delete the data by consuming it
        savedStateRegistry.consumeRestoredStateForKey(
            SAVED_STATE_REGISTRY_KEY,
        )
    }
}
