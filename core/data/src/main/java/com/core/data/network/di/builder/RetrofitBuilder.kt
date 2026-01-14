package com.core.data.network.di.builder

import com.core.data.environment.model.Environment
import dagger.assisted.AssistedInject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * RetrofitBuilder is a utility class responsible for building Retrofit instances with various configurations.
 * It provides methods to create Retrofit services with custom base URLs and interceptors.
 */
class RetrofitBuilder @AssistedInject constructor(
    private val retrofitBuilder: Retrofit.Builder,
    private val okHttpClient: OkHttpClient,
) {
    /**
     * Builds a Retrofit service instance for a given interface.
     *
     * This function constructs a Retrofit service based on the provided
     * environment, target interface class, and optional interceptors. It configures
     * the base URL from the `environment` and optionally applies a list of interceptors.
     * Finally, it creates an instance of the service interface using Retrofit's
     * `create` method.
     *
     * @param kClass The class representing the target service interface. This is the interface
     *               that Retrofit will create an implementation for. Must be a non-nullable class.
     * @param environment An `Environment` object containing configuration details,
     *                    including the base API URL.
     * @param interceptors A list of `Interceptor` instances to be added to the OkHttp client
     *                     underlying the Retrofit instance. These interceptors will be executed
     *                     in the order they are provided for each network request.
     * @return An instance of the service interface `T`, ready to be used for making API calls.
     * @throws IllegalArgumentException if `kClass` is null or not a valid interface class.
     *
     * @sample
     * ```
     *  interface MyApiService {
     *      @GET("/users")
     *      fun getUsers(): Call<List<User>>
     *  }
     *
     *  val environment = MyEnvironment("https://api.example.com")
     *  val interceptors = listOf(MyLoggingInterceptor(), MyAuthInterceptor())
     *  val myApiService = build(MyApiService::class.java, environment, interceptors)
     *
     *  // Now you can use myApiService to make network calls.
     *  val call = myApiService.getUsers()
     *  // ...
     * ```
     */
    fun <T : Any> build(kClass: Class<T>, environment: Environment, interceptors: List<Interceptor>): T =
        retrofitBuilder
            .baseUrl(environment.apiUrl)
            .build()
            .apply {
                if (interceptors.isNotEmpty()) {
                    applyCustomInterceptors(interceptors = interceptors)
                }
            }.create(kClass)

    /**
     * Builds a Retrofit service instance for a given class with a specified URL and optional interceptors.
     *
     * This function creates a Retrofit instance with the provided base URL and applies custom interceptors if any are provided.
     * It then generates an implementation of the specified service interface using the built Retrofit instance.
     *
     * @param kClass The Kotlin class representing the service interface (e.g., `MyApiService::class.java`).
     * @param url The base URL for the API endpoints.
     * @param interceptors A list of [Interceptor] instances to be applied to the OkHttpClient.
     *                     These interceptors will be executed in the order they are provided.
     *                     If the list is empty, no custom interceptors will be added.
     * @return An instance of the service interface [T] that can be used to make API calls.
     *
     * @throws IllegalArgumentException if the provided base URL is invalid.
     * @throws IllegalStateException if Retrofit configuration fails.
     * @throws RuntimeException if Service interface creation fails.
     *
     * Example:
     * ```
     * val myApiService = build(MyApiService::class.java, "https://api.example.com/", listOf(loggingInterceptor))
     * val response = myApiService.getData().execute()
     * ```
     */
    fun <T> build(kClass: Class<T>, url: String, interceptors: List<Interceptor>): T = retrofitBuilder
        .baseUrl(url)
        .build()
        .run {
            if (interceptors.isNotEmpty()) {
                applyCustomInterceptors(interceptors = interceptors)
            } else {
                this
            }
        }.create(kClass)

    /**
     * Applies a list of custom interceptors to the OkHttpClient used by Retrofit.
     *
     * This function takes a list of [Interceptor] instances and adds them to a new
     * OkHttpClient. This new client then replaces the existing one within a new
     * Retrofit instance, which is returned.  The original Retrofit instance is not modified.
     *
     * The order in which interceptors are added to the OkHttpClient matters. Interceptors
     * are executed in the order they are added.
     *
     * @param interceptors A list of [Interceptor] instances to be added to the OkHttpClient.
     *                     If an empty list is provided, no new interceptors are added.
     *                     Must not contain null elements.
     * @return A new [Retrofit] instance with the specified interceptors applied.
     * @throws IllegalArgumentException if the `interceptors` list contains null elements.
     */
    private fun Retrofit.applyCustomInterceptors(interceptors: List<Interceptor>): Retrofit {
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
