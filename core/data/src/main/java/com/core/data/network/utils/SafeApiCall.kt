package com.core.data.network.utils

import com.core.data.network.model.ApiResult
import retrofit2.Response

fun <T : Any> safeApiCall(
    execute: () -> Response<T>
): ApiResult<T> {
    return try {
        val response = execute()
        val body = response.body()

        if (response.isSuccessful) {
            return if (body != null) {
                ApiResult.Success(data = body)
            } else {
                ApiResult.NoContent()
            }
        }

        return ApiResult.Error(
            httpCode = response.code(),
            response = response.errorBody()?.string()
        )
    } catch (e: Throwable) {
        ApiResult.Exception(exception = e)
    }
}
