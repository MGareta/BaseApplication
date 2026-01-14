package com.core.data.activityResult.manager

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract

@SuppressWarnings("kotlin:S6517")
interface IActivityResultManager {
    /**
     * Requests a result from an activity using the provided [ActivityResultContract].
     *
     * This function utilizes the [ActivityResultRegistry] to launch an activity and obtain a result.
     * It handles the registration and unregistration of the [ActivityResultLauncher], as well as
     * the management of pending results.
     *
     * @param contract The [ActivityResultContract] used to define the interaction with the activity.
     * @param input The input data to be passed to the activity.
     * @param I The type of the input data.
     * @param O The type of the output data (result).
     * @param C The type of the [ActivityResultContract].
     * @return The result obtained from the activity, or `null` if the activity cannot be determined or a problem occurred during launch.
     */
    suspend fun <I, O, C : ActivityResultContract<I, O>> requestResult(contract: C, input: I): O?
}
