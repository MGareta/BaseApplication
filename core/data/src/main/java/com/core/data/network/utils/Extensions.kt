package com.core.data.network.utils

import com.core.data.network.model.ApiResult
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Executes the given [execute] function if this [ApiResult] is a [ApiResult.Success].
 *
 * This function provides a convenient way to perform an action when an API call
 * represented by this [ApiResult] is successful.  It allows you to handle the
 * successful data ([T]) within the provided lambda.
 *
 * The `apply` scope function is used so that the original `ApiResult` instance
 * is returned, enabling method chaining.
 *
 * @param execute The suspend function to be executed with the successful data.
 *                It takes the data of type [T] as a parameter.
 * @return The original [ApiResult] instance, regardless of whether the execution block was run.
 *         This enables method chaining for further operations on the result.
 * @param R The return type of the [execute] function. This can be anything because it's not used by `onSuccess` itself.
 * @param T The type of the data in the [ApiResult.Success] case.
 * @receiver This function is an extension function for [ApiResult<T>], acting on the instance of `ApiResult`.
 */
suspend fun <R, T> ApiResult<T>.onSuccess(execute: suspend (T) -> R): ApiResult<T> = apply {
    if (this is ApiResult.Success<T>) {
        execute(data)
    }
}

/**
 * Executes the given [execute] lambda if the [ApiResult] is of type [ApiResult.NoContent].
 *
 * This function allows you to perform a specific action when an API request
 * successfully returns a 204 No Content response. It essentially acts as a
 * conditional handler for [ApiResult.NoContent] states.
 *
 * The original [ApiResult] is returned, regardless of whether the [execute] lambda
 * was invoked or not. This allows for method chaining.
 *
 * @param R The return type of the [execute] lambda.
 * @param T The type of data contained within the [ApiResult] (if it were successful and not NoContent).
 * @param execute A suspend lambda to be executed when the [ApiResult] is [ApiResult.NoContent].
 * @return The original [ApiResult] instance.
 *
 * @see ApiResult
 * @see ApiResult.NoContent
 *
 * Example usage:
 * ```kotlin
 * val result: ApiResult<MyData> = myApi.getData()
 * result.onNoContent {
 *     println("Data retrieval was successful, but there was no content to return.")
 *     // Perform any necessary actions for a NoContent response.
 *     analytics.logEvent("no_content_received")
 * }
 * .onSuccess { data ->
 *     println("Received data: $data")
 * }
 * .onFailure { error ->
 *     println("Error retrieving data: $error")
 * }
 * ```
 */
suspend fun <R, T> ApiResult<T>.onNoContent(execute: suspend () -> R): ApiResult<T> = apply {
    if (this is ApiResult.NoContent<T>) {
        execute()
    }
}

/**
 * Executes a given [execute] block if the [ApiResult] is in an error state ([ApiResult.Error]).
 *
 * This function allows you to handle errors that occurred during an API call, providing the HTTP
 * status code and the error response body (if available). It only executes the provided lambda if
 * the `ApiResult` is an instance of [ApiResult.Error]. If the result is not an error, this
 * function does nothing.
 *
 * @param execute A suspend lambda that will be executed if the [ApiResult] is an error.
 *                It receives two parameters:
 *                - `httpCode`: The HTTP status code of the error response.
 *                - `response`: The body of the error response, as a string (may be null).
 * @return The original [ApiResult] instance, allowing for method chaining.
 * @see ApiResult
 * @see ApiResult.Error
 *
 * @sample
 * ```
 * val result: ApiResult<User> = try {
 *   // Make an API call that might fail
 *   ApiResult.Success(apiService.getUser())
 * } catch(e: Exception) {
 *    ApiResult.Error(500, "Internal Server Error")
 * }
 *
 * result.onError { code, error ->
 *   println("API call failed with code: $code and error: $error")
 *   // Handle the error, e.g., log it, display an error message, etc.
 * }.onSuccess { user ->
 *      println("User name : ${user.name}")
 * }
 * ```
 */
suspend fun <R, T> ApiResult<T>.onError(execute: suspend (httpCode: Int, response: String?) -> R): ApiResult<T> =
    apply {
        if (this is ApiResult.Error<T>) {
            execute(httpCode, response)
        }
    }

/**
 * Executes the provided [execute] function if the [ApiResult] is an [ApiResult.Exception].
 *
 * This function allows you to handle exceptions that occur during API calls in a concise way.
 * It only executes the [execute] function if the current [ApiResult] instance represents an error state (i.e., [ApiResult.Exception]).
 *
 * @param R The return type of the [execute] function. This type is independent of the [T] type of the [ApiResult].
 * @param T The data type wrapped by the [ApiResult].
 * @param execute A suspend function that takes a [Throwable] representing the exception as input and returns a value of type [R].
 * @return The original [ApiResult] instance. This allows for chaining of operations.
 *
 * Example Usage:
 * ```kotlin
 * val apiResult = myApiService.getData()
 * apiResult
 *     .onSuccess { data ->
 *         // Handle successful data retrieval
 *         println("Data: $data")
 *     }
 *     .onException { exception ->
 *         // Handle the exception
 *         println("An exception occurred: ${exception.message}")
 *         // You can also return a default value or perform other actions
 *         return@onException "Default Value"
 *     }
 *     .onFailure {  code, message ->
 *         println("Failure with code: $code and message $message")
 *     }
 * ```
 *
 */
suspend fun <R, T> ApiResult<T>.onException(execute: suspend (exception: Throwable) -> R): ApiResult<T> = apply {
    if (this is ApiResult.Exception<T>) {
        execute(exception)
    }
}

/**
 * Maps the successful result of an `ApiResult<T>` to a new `ApiResult<R>` using the provided [transform] function.
 *
 * This function allows you to transform the `data` within a successful `ApiResult.Success` case into a new result of type `R`,
 * while propagating the original `ApiResult` in cases of `ApiResult.Error`, `ApiResult.Exception`, or `ApiResult.NoContent`.
 *
 * The [transform] function is a suspend function, allowing for asynchronous operations within the mapping process.
 *
 * If the [transform] function throws an exception, the result will be an `ApiResult.Exception` containing the thrown exception.
 *
 * @param transform A suspend function that takes the `data` of a successful `ApiResult.Success` and returns a new `ApiResult<R>`.
 * @return An `ApiResult<R>` that is either:
 *   - The result of applying the [transform] function to the `data` in case of `ApiResult.Success`.
 *   - An `ApiResult.Error` with the same `httpCode` and `response` in case of `ApiResult.Error`.
 *   - An `ApiResult.Exception` with the same `exception` in case of `ApiResult.Exception`.
 *   - An `ApiResult.NoContent` in case of `ApiResult.NoContent`.
 *   - An `ApiResult.Exception` if the [transform] function throws an exception.
 *
 * @throws Throwable if the [transform] function throws an exception. This exception will be wrapped in an `ApiResult.Exception`.
 *
 * @sample
 * ```
 * suspend fun example() {
 *     val successResult: ApiResult<Int> = ApiResult.Success(10)
 *     val transformedResult: ApiResult<String> = successResult.map { number ->
 *         ApiResult.Success("Number: $number")
 *     }
 *     println(transformedResult) // Output: ApiResult.Success(data=Number: 10)
 *
 *     val errorResult: ApiResult<Int> = ApiResult.Error(404, "Not Found")
 */
@OptIn(ExperimentalContracts::class)
suspend fun <R, T> ApiResult<T>.map(transform: suspend (value: T) -> ApiResult<R>): ApiResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is ApiResult.Success -> try {
            transform(data)
        } catch (exception: Throwable) {
            ApiResult.Exception(
                exception = exception,
            )
        }
        is ApiResult.Error -> ApiResult.Error(httpCode = httpCode, response = response)
        is ApiResult.Exception -> ApiResult.Exception(exception = exception)
        is ApiResult.NoContent -> ApiResult.NoContent()
    }
}

/**
 * Returns the encapsulated value if this instance represents [ApiResult.Success] or the
 * result of [onFailure] function if it is [ApiResult.Failure].
 *
 * This function allows to handle both success and failure cases in a concise way. If the
 * `ApiResult` is a `Success`, it will directly return the encapsulated data. If it is a
 * `Failure` or any other state that's not a `Success`, it will invoke the `onFailure` lambda
 * and return its result.
 *
 * The [onFailure] function will be invoked at most once.
 *
 * @param onFailure The lambda to execute and return its result if the `ApiResult` is not a `Success`.
 * @return The encapsulated data if the `ApiResult` is a `Success`, or the result of [onFailure] otherwise.
 *
 * @sample
 * ```kotlin
 * val successResult: ApiResult<Int> = ApiResult.Success(10)
 * val failureResult: ApiResult<Int> = ApiResult.Failure(RuntimeException("Something went wrong"))
 *
 * val successValue = successResult.getOrElse { -1 } // successValue will be 10
 * val failureValue = failureResult.getOrElse { -1 } // failureValue will be -1
 *
 * val anotherSuccessValue = successResult.getOrElse {
 *      println("This will not be printed")
 *      -1
 * } // anotherSuccessValue will be 10
 *
 * val anotherFailureValue = failureResult.getOrElse {
 *     println("Handling failure")
 *     -1
 * } // Prints "Handling failure" and anotherFailureValue will be -1
 * ```
 */
@OptIn(ExperimentalContracts::class)
fun <R, T : R> ApiResult<T>.getOrElse(onFailure: () -> R): R {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is ApiResult.Success -> this.data
        else -> onFailure()
    }
}

/**
 * Returns the value of the [ApiResult] if it's a [ApiResult.Success], otherwise returns `null`.
 *
 * This function provides a convenient way to access the value of an [ApiResult] when you're
 * not interested in handling the error case explicitly and are okay with a null value
 * representing a failure.
 *
 * @param T The type of the value contained within the [ApiResult].
 * @return The value of the [ApiResult] if it's a [ApiResult.Success], or `null` if it's a [ApiResult.Failure].
 *
 * Example:
 * ```
 * val successResult: ApiResult<String> = ApiResult.Success("Hello")
 * val failureResult: ApiResult<String> = ApiResult.Failure(Exception("Something went wrong"))
 *
 * val successValue: String? = successResult.getOrNull() // successValue will be "Hello"
 * val failureValue: String? = failureResult.getOrNull() // failureValue will be null
 * ```
 */
fun <T> ApiResult<T>.getOrNull(): T? = this.getOrElse { null }

/**
 * Retrieves the successful value from an [ApiResult]. If the result is a failure, throws an exception
 * provided by the [onFailure] lambda.
 *
 * This function is a convenience method for handling [ApiResult] instances where you want to
 * immediately throw an exception if the result is not a success. It simplifies the common pattern
 * of checking for failure and throwing a custom exception.
 *
 * @param T The type of the successful value.
 * @param onFailure A lambda that provides the [Throwable] to throw if the result is a failure.
 *                  This lambda will be invoked only if the [ApiResult] is a failure.
 * @return The successful value of type [T] if the [ApiResult] is a success.
 * @throws Throwable The exception returned by the [onFailure] lambda if the [ApiResult] is a failure.
 * @sample
 * ```kotlin
 * // Example of using getOrThrow with a custom exception
 * val result: ApiResult<Int> = ApiResult.Failure(Exception("Something went wrong"))
 * try {
 *     val value = result.getOrThrow { CustomException("Failed to retrieve value") }
 *     println("Value: $value")
 * } catch (e: CustomException) {
 *     println("Caught CustomException: ${e.message}")
 * }
 *
 * // Example of using getOrThrow with no custom exception
 * val result2: ApiResult<String> = ApiResult.Success("Hello")
 * val value2 = result2.getOrThrow { RuntimeException("Should not happen") }
 * println("value2: $value2")
 * ```
 */
fun <T> ApiResult<T>.getOrThrow(onFailure: () -> Throwable): T = this.getOrElse { throw onFailure() }
