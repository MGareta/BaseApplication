package com.core.data.activityResult.manager

import androidx.activity.result.contract.ActivityResultContract

interface IActivityResultManager {

    suspend fun <I, O, C : ActivityResultContract<I, O>> requestResult(
        contract: C,
        input: I
    ): O?
}
