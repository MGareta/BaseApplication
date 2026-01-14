package com.core.data.download.repository

import com.core.data.network.model.ApiResult
import okhttp3.ResponseBody

fun interface IDownloadRepository {
    /**
     * Downloads a file from the given URL with the specified headers.
     *
     * @param url The URL of the file to download.
     * @param headers A map of HTTP headers to include in the request.
     * @return An [ApiResult] containing either the downloaded file as a [ResponseBody] on success,
     *         or an error of type [ApiError] on failure.
     *         [ApiResult.Success] will contain the [ResponseBody] when the download succeeds.
     *         [ApiResult.Error] will contain an [ApiError] when an error occurred during the download.
     *
     *         Possible [ApiError] types are :
     *          - [ApiError.NetworkError]: if there's an issue with network connectivity.
     *          - [ApiError.ServerError]: if the server returned an error status code.
     *          - [ApiError.UnknownError]: if an unknown error occurs.
     *
     * @throws IllegalArgumentException if the URL is empty or invalid.
     *
     * @see ApiResult
     * @see ResponseBody
     */
    suspend fun downloadFile(url: String, headers: Map<String, String>): ApiResult<ResponseBody>
}
