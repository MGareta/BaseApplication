package com.core.data.network.utils

import com.core.data.network.adapter.NetworkResultCall
import com.core.data.network.model.ApiResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal fun <T : Any> executeNetworkResultCall(
    proxy: Call<T>
): ApiResult<T> {
    var result: ApiResult<T>? = null

    NetworkResultCall(proxy).enqueue(
        callback = object : Callback<ApiResult<T>> {
            override fun onResponse(
                call: Call<ApiResult<T>>,
                response: Response<ApiResult<T>>,
            ) {
                result = response.body()
            }

            override fun onFailure(call: Call<ApiResult<T>>, t: Throwable) {
                throw AssertionError(t)
            }
        }
    )

    return requireNotNull(value = result)
}