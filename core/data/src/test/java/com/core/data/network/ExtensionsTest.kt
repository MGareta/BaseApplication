package com.core.data.network

import com.core.data.HiltRobolectricBaseTest
import com.core.data.network.model.ApiResult
import com.core.data.network.utils.getOrElse
import com.core.data.network.utils.getOrNull
import com.core.data.network.utils.getOrThrow
import com.core.data.network.utils.map
import com.core.data.network.utils.onError
import com.core.data.network.utils.onException
import com.core.data.network.utils.onNoContent
import com.core.data.network.utils.onSuccess
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

@HiltAndroidTest
class ExtensionsTest : HiltRobolectricBaseTest() {

    override fun initDependencies() = Unit

    @Test
    fun onSuccessExecutesOnlyForSuccessAndReturnsOriginalResult() = test {
        val success: ApiResult<String> = ApiResult.Success(data = "value")
        var received: String? = null
        var errorCallbackCalled = false

        assertSame(success, success.onSuccess { received = it })

        ApiResult.Error<String>(httpCode = 500, response = null)
            .onSuccess { errorCallbackCalled = true }

        assertEquals("value", received)
        assertFalse(errorCallbackCalled)
    }

    @Test
    fun onNoContentExecutesOnlyForNoContentAndReturnsOriginalResult() = test {
        val noContent: ApiResult<String> = ApiResult.NoContent()
        var callbackCalls = 0

        assertSame(noContent, noContent.onNoContent { callbackCalls++ })

        ApiResult.Success(data = "value")
            .onNoContent { callbackCalls++ }

        assertEquals(1, callbackCalls)
    }

    @Test
    fun onErrorPassesErrorDetailsAndSkipsOtherResults() = test {
        val error: ApiResult<String> = ApiResult.Error(httpCode = 503, response = "unavailable")
        var received: Pair<Int, String?>? = null
        var successCallbackCalled = false

        assertSame(error, error.onError { code, body -> received = code to body })

        ApiResult.Success(data = "value")
            .onError { _, _ -> successCallbackCalled = true }

        assertEquals(503 to "unavailable", received)
        assertFalse(successCallbackCalled)
    }

    @Test
    fun onExceptionPassesExceptionAndSkipsOtherResults() = test {
        val exception = IllegalStateException("failed")
        val failure: ApiResult<String> = ApiResult.Exception(exception = exception)
        var received: Throwable? = null
        var successCallbackCalled = false

        assertSame(failure, failure.onException { received = it })

        ApiResult.Success(data = "value")
            .onException { successCallbackCalled = true }

        assertSame(exception, received)
        assertFalse(successCallbackCalled)
    }

    @Test
    fun mapTransformsSuccessAndWrapsThrownExceptions() = test {
        val success: ApiResult<String> = ApiResult.Success(data = "value")
        val mapped = success.map { ApiResult.Success(data = it.length) }
        val exception = IllegalStateException("transform failed")
        val failedMapping: ApiResult<String> = success.map { throw exception }

        assertEquals(ApiResult.Success(data = 5), mapped)
        assertTrue(failedMapping is ApiResult.Exception)
        assertSame(exception, (failedMapping as ApiResult.Exception).exception)
    }

    @Test
    fun mapPassesThroughNonSuccessResultsWithoutCallingTransform() = test {
        val error: ApiResult<String> = ApiResult.Error(httpCode = 404, response = "not found")
        val exception = IllegalStateException("request failed")
        val failure: ApiResult<String> = ApiResult.Exception(exception = exception)
        val noContent: ApiResult<String> = ApiResult.NoContent()
        var transformCalls = 0

        val mappedError = error.map {
            transformCalls++
            ApiResult.Success(it.length)
        }

        val mappedFailure = failure.map {
            transformCalls++
            ApiResult.Success(it.length)
        }

        val mappedNoContent = noContent.map {
            transformCalls++
            ApiResult.Success(it.length)
        }

        assertEquals(error, mappedError)
        assertTrue(mappedFailure is ApiResult.Exception)
        assertSame(exception, (mappedFailure as ApiResult.Exception).exception)
        assertTrue(mappedNoContent is ApiResult.NoContent)
        assertEquals(0, transformCalls)
    }

    @Test
    fun getOrElseReturnsSuccessValueOrFallback() = test {
        var fallbackCalls = 0
        val success = ApiResult.Success(data = "value").getOrElse {
            fallbackCalls++
            "fallback"
        }

        val error: ApiResult<String> = ApiResult.Error(httpCode = 500, response = null)
        val fallback = error.getOrElse {
            fallbackCalls++
            "fallback"
        }

        assertEquals("value", success)
        assertEquals("fallback", fallback)
        assertEquals(1, fallbackCalls)
    }

    @Test
    fun getOrNullReturnsOnlySuccessValue() = test {
        assertEquals("value", ApiResult.Success(data = "value").getOrNull())
        assertNull(ApiResult.Error<String>(httpCode = 500, response = null).getOrNull())
    }

    @Test
    fun getOrThrowReturnsSuccessValueOrThrowsFallback() = test {
        val exception = IllegalStateException("request failed")
        val error: ApiResult<String> = ApiResult.Error(httpCode = 500, response = null)

        assertEquals("value", ApiResult.Success("value").getOrThrow { exception })
        assertSame(
            exception,
            assertThrows(IllegalStateException::class.java) {
                error.getOrThrow { exception }
            }
        )
    }
}
