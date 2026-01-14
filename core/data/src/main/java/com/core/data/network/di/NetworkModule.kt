package com.core.data.network.di

import android.content.Context
import com.core.data.environment.manager.IEnvironmentManager
import com.core.data.environment.model.Environment
import com.core.data.network.adapter.NetworkResultCallAdapterFactory
import com.core.data.network.ssl.NetworkTrustManager
import com.core.data.network.ssl.disableSSLPinning
import com.core.data.network.ssl.enableSSLPinning
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val DEFAULT_TIMEOUT = 30L

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun providesNetworkTrustManager(@ApplicationContext context: Context): NetworkTrustManager = NetworkTrustManager(
        context = context,
    )

    @Provides
    @Singleton
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        environmentManager: IEnvironmentManager,
        networkTrustManager: NetworkTrustManager,
    ): OkHttpClient = OkHttpClient
        .Builder()
        .apply {
            if (environmentManager.environment == Environment.PROD()) {
                enableSSLPinning(networkTrustManager = networkTrustManager)
            } else {
                disableSSLPinning(networkTrustManager = networkTrustManager)
            }

            addInterceptor(httpLoggingInterceptor)
            readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        }.build()

    @Provides
    @Singleton
    fun providesConverterFactory(): Converter.Factory {
        val contentType = "application/json".toMediaType()
        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            useAlternativeNames = true
            encodeDefaults = true
            explicitNulls = true
            coerceInputValues = true
            allowStructuredMapKeys = true
        }

        return json.asConverterFactory(contentType = contentType)
    }

    @Provides
    @Singleton
    fun providesRetrofitBuilder(httpClient: OkHttpClient, converterFactory: Converter.Factory): Retrofit.Builder =
        Retrofit
            .Builder()
            .client(httpClient)
            .addCallAdapterFactory(NetworkResultCallAdapterFactory.create())
            .addConverterFactory(converterFactory)
}
