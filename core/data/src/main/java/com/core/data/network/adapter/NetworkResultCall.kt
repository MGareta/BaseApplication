package com.core.data.network.adapter

import com.core.data.network.model.ApiResult
import com.core.data.network.utils.safeApiCall
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkResultCall<T : Any>(
    private val proxy: Call<T>
) : Call<ApiResult<T>> {

    override fun enqueue(
        callback: Callback<ApiResult<T>>
    ) {
        proxy.enqueue(object : Callback<T> {
            override fun onResponse(
                call: Call<T>,
                response: Response<T>
            ) {
                val networkResult = safeApiCall { response }

                callback.onResponse(
                    this@NetworkResultCall,
                    Response.success(networkResult)
                )
            }

            override fun onFailure(
                call: Call<T>,
                t: Throwable
            ) {
                val networkResult = ApiResult.Exception<T>(t)

                callback.onResponse(
                    this@NetworkResultCall,
                    Response.success(networkResult)
                )
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
