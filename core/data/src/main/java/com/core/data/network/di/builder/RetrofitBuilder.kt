package com.core.data.network.di.builder

import com.core.data.environment.model.Environment
import dagger.assisted.AssistedInject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class RetrofitBuilder @AssistedInject constructor(
    private val retrofitBuilder: Retrofit.Builder,
    private val okHttpClient: OkHttpClient
) {

    fun <T : Any> build(
        kClass: Class<T>,
        environment: Environment,
        interceptors: List<Interceptor>
    ): T = retrofitBuilder
        .baseUrl(environment.apiUrl)
        .build()
        .let { retrofit ->
            if (interceptors.isNotEmpty()) {
                retrofit.applyCustomInterceptors(interceptors = interceptors)
            } else {
                retrofit
            }
        }.create(kClass)

    fun <T> build(
        kClass: Class<T>,
        url: String,
        interceptors: List<Interceptor>
    ): T = retrofitBuilder
        .baseUrl(url)
        .build()
        .let { retrofit ->
            if (interceptors.isNotEmpty()) {
                retrofit.applyCustomInterceptors(interceptors = interceptors)
            } else {
                retrofit
            }
        }.create(kClass)

    private fun Retrofit.applyCustomInterceptors(
        interceptors: List<Interceptor>
    ): Retrofit {
        val overrideOkHttpClient = okHttpClient
            .newBuilder()
            .apply {
                interceptors.forEach {
                    addInterceptor(it)
                }
            }.build()

        return newBuilder()
            .apply {
                client(overrideOkHttpClient)
            }.build()
    }
}
