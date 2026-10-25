package com.core.data.network.utils

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class ImmediateCall<T : Any>(
    private val response: Response<T>? = null,
    private val failure: Throwable? = null,
) : Call<T> {
    private var executed = false
    private var canceled = false

    override fun enqueue(callback: Callback<T>) {
        executed = true
        val callFailure = failure
        if (callFailure != null) {
            callback.onFailure(this, callFailure)
        } else {
            callback.onResponse(this, requireNotNull(response))
        }
    }

    override fun execute(): Response<T> {
        executed = true
        failure?.let { throw it }
        return requireNotNull(response)
    }

    override fun clone(): Call<T> = ImmediateCall(
        response = response,
        failure = failure,
    )

    override fun request(): Request = Request.Builder()
        .url("https://example.test/")
        .build()

    override fun timeout(): Timeout = Timeout.NONE

    override fun isExecuted(): Boolean = executed

    override fun isCanceled(): Boolean = canceled

    override fun cancel() {
        canceled = true
    }
}