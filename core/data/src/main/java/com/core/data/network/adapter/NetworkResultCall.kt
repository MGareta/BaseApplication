package com.core.data.network.adapter

import com.core.data.network.model.ApiResult
import com.core.data.network.utils.safeApiCall
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A custom [Call] implementation that wraps another [Call] and transforms its
 * response into an [ApiResult].
 *
 * This class is designed to be used with Retrofit's [CallAdapter] to handle
 * API responses consistently, encapsulating them within an [ApiResult] which
 * represents either a successful result, a failure due to an HTTP error, or
 * an exception.
 *
 * @param T The type of the successful data returned by the underlying API call.
 *          Must be a non-nullable type.
 * @property proxy The underlying [Call] that this class wraps.
 */
class NetworkResultCall<T : Any>(private val proxy: Call<T>) : Call<ApiResult<T>> {
    /**
     * Enqueues the request to be executed asynchronously.
     *
     * This method handles the asynchronous execution of the network call using the underlying proxy call.
     * It wraps the response or failure from the proxy call into an `ApiResult` and then passes it to the provided callback.
     *
     * @param callback The callback to be invoked when the network request completes, either successfully or with an error.
     *                 This callback receives a `Response<ApiResult<T>>` object, where `ApiResult<T>` represents
     *                 either a successful response with data of type `T` or an exception.
     *
     * The `onResponse` method of the provided callback is always invoked in this implementation.
     *
     * Internally:
     *  - It delegates the actual enqueueing to the `proxy` object.
     *  - It creates an anonymous `Callback<T>` that intercepts the proxy's response or failure.
     *  - In `onResponse`, it uses `safeApiCall` to convert the raw `Response<T>` into an `ApiResult<T>`.
     *  - In `onFailure`, it creates an `ApiResult.Exception<T>` to encapsulate the error.
     *  - It then packages the `ApiResult<T>` in another `Response` object and calls `callback.onResponse` with the result.
     */
    override fun enqueue(callback: Callback<ApiResult<T>>) {
        proxy.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val networkResult = safeApiCall { response }
                callback.onResponse(this@NetworkResultCall, Response.success(networkResult))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val networkResult = ApiResult.Exception<T>(t)
                callback.onResponse(this@NetworkResultCall, Response.success(networkResult))
            }
        })
    }

    override fun execute(): Response<ApiResult<T>> = throw NotImplementedError()

    override fun clone(): Call<ApiResult<T>> = NetworkResultCall(proxy.clone())

    override fun request(): Request = proxy.request()

    override fun timeout(): Timeout = proxy.timeout()

    override fun isExecuted(): Boolean = proxy.isExecuted

    override fun isCanceled(): Boolean = proxy.isCanceled

    override fun cancel() = proxy.cancel()
}
