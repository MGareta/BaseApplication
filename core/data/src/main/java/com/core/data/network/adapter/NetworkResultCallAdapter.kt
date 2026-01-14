package com.core.data.network.adapter

import com.core.data.network.model.ApiResult
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

/**
 * A [CallAdapter] which adapts a [Call] of type [Type] to a [Call] of type [ApiResult]<[Type]>.
 *
 * This adapter is responsible for converting Retrofit's default [Call] behavior to work with our custom [ApiResult]
 * sealed class, which encapsulates successful and error states from API responses.
 *
 * It essentially wraps the original Retrofit [Call] with a [NetworkResultCall] that handles the conversion of the
 * raw HTTP response into an [ApiResult].
 *
 * @property resultType The expected type of the successful response body. This is used to determine the
 *                     type parameter of the [ApiResult] and the original [Call].
 *                     For instance, if you expect a `User` object, `resultType` will be `User::class.java`.
 */
class NetworkResultCallAdapter(private val resultType: Type) : CallAdapter<Type, Call<ApiResult<Type>>> {
    override fun responseType(): Type = resultType

    /**
     * Adapts a Retrofit [Call] to a [NetworkResultCall] that wraps the response in an [ApiResult].
     *
     * This function is part of a custom [CallAdapter] and is responsible for transforming a standard
     * Retrofit [Call] that returns a raw response type (e.g., `Call<User>`) into a [Call] that
     * returns an [ApiResult] (e.g., `Call<ApiResult<User>>`). This allows for a more robust and
     * consistent way of handling network responses, including success, errors, and empty bodies.
     *
     * @param call The original Retrofit [Call] to adapt.
     * @return A new [NetworkResultCall] that wraps the original call and returns an [ApiResult].
     */
    override fun adapt(call: Call<Type>): Call<ApiResult<Type>> = NetworkResultCall(call)
}
