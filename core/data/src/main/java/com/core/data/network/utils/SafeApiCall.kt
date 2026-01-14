package com.core.data.network.utils

import com.core.data.network.model.ApiResult
import retrofit2.Response

/**
 * Executes a network API call safely, handling potential errors and returning a structured result.
 *
 * This function wraps an API call within a try-catch block to gracefully handle exceptions.
 * It processes the HTTP response, checking for success and handling different scenarios like
 * successful responses with data, successful responses without data, and error responses.
 *
 * @param execute A lambda function representing the API call to be executed. This function
 *                should return a [Response] object from Retrofit (or a similar library).
 * @param T The type of the data expected in a successful response body. Must be a non-nullable type.
 * @return An [ApiResult] object encapsulating the result of the API call. This can be one of:
 *         - [ApiResult.Success]: If the API call is successful and the response body is not null.
 *         - [ApiResult.NoContent]: If the API call is successful but the response body is null.
 *         - [ApiResult.Error]: If the API call returns an error response (e.g., HTTP 404, 500).
 *         - [ApiResult.Exception]: If an exception occurred during the API call (e.g., network error).
 *
 * @throws [Throwable] If an unexpected exception occurs outside the network call itself (unlikely).
 *
 * Example Usage:
 * ```
 * val result = safeApiCall { myApiService.getUserProfile(userId) }
 * when (result) {
 *     is ApiResult.Success -> {
 *         val userProfile = result.data
 *         // Process the successful data
 *     }
 *     is ApiResult.NoContent -> {
 *        // Handle case where the server returned 200 but with no data
 *     }
 *     is ApiResult.Error -> {
 *         val errorCode = result.httpCode
 *         val errorMessage = result.response
 *         // Handle the error based on error code and message.
 *     }
 *     is ApiResult.Exception -> {
 *         val exception = result.exception
 *         // Handle the exception.
 *     }
 * }
 * ```
 */
fun <T : Any> safeApiCall(execute: () -> Response<T>): ApiResult<T> {
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
            response = response.errorBody()?.string(),
        )
    } catch (e: Throwable) {
        ApiResult.Exception(exception = e)
    }
}
