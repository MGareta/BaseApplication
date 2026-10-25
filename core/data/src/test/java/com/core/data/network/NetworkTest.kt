package com.core.data.network

import com.core.data.HiltRobolectricBaseTest
import com.core.data.environment.model.Environment
import com.core.data.network.adapter.NetworkResultCall
import com.core.data.network.adapter.NetworkResultCallAdapter
import com.core.data.network.adapter.NetworkResultCallAdapterFactory
import com.core.data.network.api.NetworkApiService
import com.core.data.network.di.factory.RetrofitFactory
import com.core.data.network.model.ApiResult
import com.core.data.network.model.NetworkPayload
import com.core.data.network.ssl.NetworkTrustManager
import com.core.data.network.utils.ImmediateCall
import com.core.data.network.utils.NetworkService
import com.core.data.network.utils.executeNetworkResultCall
import com.core.data.network.utils.safeApiCall
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.SSLSession

@HiltAndroidTest
class NetworkTest : HiltRobolectricBaseTest() {

    @Inject
    lateinit var retrofitFactory: RetrofitFactory

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var retrofitBuilder: Retrofit.Builder

    @Inject
    lateinit var networkTrustManager: NetworkTrustManager

    @Inject
    lateinit var httpLoggingInterceptor: HttpLoggingInterceptor

    override fun initDependencies() = Unit

    @Test
    fun networkModuleConfiguresOkHttpClientAndLoggingInterceptor() = test {
        assertEquals(HttpLoggingInterceptor.Level.BODY, httpLoggingInterceptor.level)
        assertEquals(listOf(httpLoggingInterceptor), okHttpClient.interceptors)
        assertEquals(30_000, okHttpClient.connectTimeoutMillis)
        assertEquals(30_000, okHttpClient.readTimeoutMillis)
        assertEquals(30_000, okHttpClient.writeTimeoutMillis)
        assertTrue(okHttpClient.hostnameVerifier.verify("api.example.test", mockk()))
    }

    @Test
    fun nonProductionEnvironmentDisablesSslPinning() = test {
        val session = mockk<SSLSession>()

        assertNotNull(okHttpClient.sslSocketFactory)
        assertTrue(okHttpClient.hostnameVerifier.verify("api.example.test", session))
    }

    @Test
    fun insecureTrustManagerAcceptsClientAndServerChains() = test {
        val trustManager = networkTrustManager.insecureTrustManager
        val certificates = emptyArray<X509Certificate>()

        assertEquals(0, trustManager.acceptedIssuers.size)
        trustManager.checkClientTrusted(certificates, "RSA")
        trustManager.checkServerTrusted(certificates, "RSA")
    }

    @Test
    fun secureTrustManagerRejectsEmptyClientAndServerChains() = test {
        val trustManager = networkTrustManager.secureTrustManager
        val certificates = emptyArray<X509Certificate>()

        assertThrows(IllegalArgumentException::class.java) {
            trustManager.checkClientTrusted(certificates, "RSA")
        }

        assertThrows(IllegalArgumentException::class.java) {
            trustManager.checkServerTrusted(certificates, "RSA")
        }
    }

    @Test
    fun networkModuleConfiguresRetrofitAdapterAndConverter() = test {
        val retrofit = retrofitBuilder
            .baseUrl("https://example.test/")
            .build()

        val resultMethod = NetworkService::class.java.getDeclaredMethod("result")
        val converter = retrofit.responseBodyConverter<NetworkPayload>(
            NetworkPayload::class.java,
            emptyArray()
        )

        val body = """{"value":"response","futureField":true}"""
            .toResponseBody(
                contentType = "application/json".toMediaType()
            )

        assertSame(okHttpClient, retrofit.callFactory())
        assertTrue(
            retrofit.callAdapter(
                resultMethod.genericReturnType,
                resultMethod.annotations
            ) is NetworkResultCallAdapter
        )
        assertEquals(
            NetworkPayload(value = "response"),
            converter.convert(body)
        )
    }

    @Test
    fun retrofitFactoryBuildsBothServicesWithoutCustomInterceptors() = test {
        val retrofitBuilder = retrofitFactory.create()

        val environmentService = retrofitBuilder.build(
            kClass = NetworkApiService::class.java,
            environment = Environment.DEV(apiUrl = "https://environment.test/api/"),
            interceptors = emptyList()
        )

        val explicitUrlService = retrofitBuilder.build(
            kClass = NetworkApiService::class.java,
            url = "https://custom.test/v1/",
            interceptors = emptyList()
        )

        assertEquals(
            listOf(
                "https://environment.test/api/response",
                "https://custom.test/v1/response"
            ),
            listOf(
                environmentService.response().request().url.toString(),
                explicitUrlService.response().request().url.toString()
            )
        )
    }

    @Test
    fun retrofitFactoryAppliesCustomInterceptorToBothBuildOverloads() = test {
        val retrofitBuilder = retrofitFactory.create()
        var interceptorCalls = 0

        val interceptor = Interceptor { chain ->
            interceptorCalls++
            okhttp3.Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("intercepted".toResponseBody(
                    contentType = "text/plain".toMediaType())
                )
                .build()
        }

        val environmentCall = retrofitBuilder.build(
            kClass = NetworkApiService::class.java,
            environment = Environment.DEV(apiUrl = "https://network.test/api/"),
            interceptors = listOf(interceptor)
        ).response()

        val explicitUrlCall = retrofitBuilder.build(
            kClass = NetworkApiService::class.java,
            url = "https://custom.test/v1/",
            interceptors = listOf(interceptor)
        ).response()

        val calls = listOf(environmentCall, explicitUrlCall)

        assertEquals(
            listOf(
                "https://network.test/api/response",
                "https://custom.test/v1/response"
            ),
            calls.map { it.request().url.toString() }
        )

        assertEquals(
            listOf("intercepted", "intercepted"),
            calls.map { it.execute().body()?.string() }
        )

        assertEquals(2, interceptorCalls)
    }

    @Test
    fun safeApiCallReturnsSuccessForSuccessfulResponse() = test {
        assertEquals(
            ApiResult.Success(data = "response"),
            safeApiCall { Response.success("response") }
        )
    }

    @Test
    fun safeApiCallReturnsNoContentForEmptySuccessfulResponse() = test {
        val response: Response<String> = Response.success<String>(
            204,
            null
        )

        assertTrue(safeApiCall { response } is ApiResult.NoContent)
    }

    @Test
    fun safeApiCallPreservesHttpErrorCodeAndBody() = test {
        val response = Response.error<String>(
            404,
            "not found".toResponseBody("text/plain".toMediaType())
        )

        assertEquals(
            ApiResult.Error<String>(
                httpCode = 404,
                response = "not found"
            ),
            safeApiCall<String> { response }
        )
    }

    @Test
    fun safeApiCallWrapsThrownException() = test {
        val exception = IOException("offline")

        val result = safeApiCall<String> {
            throw exception
        }

        assertTrue(result is ApiResult.Exception)
        assertSame(exception, (result as ApiResult.Exception).exception)
    }

    @Test
    fun networkResultCallWrapsSuccessfulResponse() = test {
        assertEquals(
            ApiResult.Success(data = "response"),
            executeNetworkResultCall(
                proxy = ImmediateCall(
                    response = Response.success("response")
                )
            )
        )
    }

    @Test
    fun networkResultCallWrapsHttpErrorResponse() = test {
        val response = Response.error<String>(
            503,
            "unavailable".toResponseBody(
                contentType = "text/plain".toMediaType()
            )
        )

        assertEquals(
            ApiResult.Error<String>(httpCode = 503, response = "unavailable"),
            executeNetworkResultCall<String>(proxy = ImmediateCall(response = response))
        )
    }

    @Test
    fun networkResultCallWrapsTransportFailure() = test {
        val exception = IOException("offline")
        val result = executeNetworkResultCall<String>(
            proxy = ImmediateCall(failure = exception)
        )

        assertTrue(result is ApiResult.Exception)
        assertSame(exception, (result as ApiResult.Exception).exception)
    }

    @Test
    fun adapterFactoryHandlesOnlyCallsReturningApiResult() = test {
        val retrofit = retrofitBuilder
            .baseUrl("https://example.test/")
            .build()

        val factory = NetworkResultCallAdapterFactory.create()
        val resultMethod = NetworkService::class.java.getDeclaredMethod("result")
        val rawMethod = NetworkService::class.java.getDeclaredMethod("raw")

        val adapter = factory.get(
            returnType = resultMethod.genericReturnType,
            annotations = resultMethod.annotations,
            retrofit = retrofit
        )

        assertTrue(adapter is CallAdapter<*, *>)
        assertEquals(String::class.java, adapter?.responseType())
        assertNull(factory.get(
            returnType = rawMethod.genericReturnType,
            annotations = rawMethod.annotations,
            retrofit = retrofit
        ))
    }

    @Test
    fun adapterWrapsCallAndDelegatesResult() = test {
        val retrofit = retrofitBuilder
            .baseUrl("https://example.test/")
            .build()

        val resultMethod = NetworkService::class.java.getDeclaredMethod("result")

        @Suppress("UNCHECKED_CAST")
        val adapter = NetworkResultCallAdapterFactory.create().get(
            returnType = resultMethod.genericReturnType,
            annotations = resultMethod.annotations,
            retrofit = retrofit
        ) as CallAdapter<String, Call<ApiResult<String>>>

        val proxy = ImmediateCall(response = Response.success("response"))
        val adapted = adapter.adapt(proxy)
        var result: ApiResult<String>? = null

        assertTrue(adapted is NetworkResultCall<*>)
        assertEquals(proxy.request().url, adapted.request().url)

        adapted.enqueue(object : Callback<ApiResult<String>> {
            override fun onResponse(
                call: Call<ApiResult<String>>,
                response: Response<ApiResult<String>>
            ) {
                result = response.body()
            }

            override fun onFailure(call: Call<ApiResult<String>>, t: Throwable) {
                throw AssertionError(t)
            }
        })

        assertEquals(ApiResult.Success(data = "response"), result)
        assertTrue(proxy.isExecuted)
    }
}
