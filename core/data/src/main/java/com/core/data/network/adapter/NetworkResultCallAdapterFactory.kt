package com.core.data.network.adapter

import com.core.data.network.model.ApiResult
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A {@link CallAdapter.Factory} that handles responses wrapped in {@link ApiResult}.
 * <p>
 * This factory allows Retrofit to adapt network responses into {@link Call} objects
 * whose body is wrapped in an {@link ApiResult}. This enables unified handling of
 * success and error scenarios from network operations.
 * </p>
 * <p>
 * To use this factory, add it to your {@link Retrofit} instance:
 * <pre>
 *     Retrofit retrofit = new Retrofit.Builder()
 *         .baseUrl("https://api.example.com/")
 *         .addConverterFactory(GsonConverterFactory.create())
 *         .addCallAdapterFactory(NetworkResultCallAdapterFactory.create())
 *         .build();
 * </pre>
 * </p>
 * <p>
 * Example usage with an interface:
 * <pre>
 * interface MyService {
 *     {@literal @}GET("users")
 *     Call<ApiResult<List<User>>> getUsers();
 * }
 * </pre>
 * </p>
 * <p>
 * In the example above, when you call `getUsers()`, the returned {@link Call} will
 * produce an {@link ApiResult} that encapsulates either a successful `List<User>`
 * or an error.
 * </p>
 * @see ApiResult
 * @see NetworkResultCallAdapter
 */
class NetworkResultCallAdapterFactory private constructor() : CallAdapter.Factory() {
    companion object {
        fun create(): NetworkResultCallAdapterFactory = NetworkResultCallAdapterFactory()
    }

    /**
     * Determines whether this factory can handle the given return type and creates a [CallAdapter] if possible.
     *
     * This method is called by Retrofit to determine if this factory can adapt the given return type.
     * It checks if the return type is a [Call] and if the generic type of the [Call] is an [ApiResult].
     * If both conditions are true, it creates a [NetworkResultCallAdapter] to handle the response.
     *
     * @param returnType The return type of the API method.
     * @param annotations The annotations on the API method.
     * @param retrofit The Retrofit instance.
     * @return A [CallAdapter] if this factory can handle the return type, or null otherwise.
     *
     * @see NetworkResultCallAdapter
     */
    override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        val callType = getParameterUpperBound(0, returnType as ParameterizedType)
        if (getRawType(callType) != ApiResult::class.java) {
            return null
        }

        val resultType = getParameterUpperBound(0, callType as ParameterizedType)
        return NetworkResultCallAdapter(resultType)
    }
}
