package com.core.data.download.manager

import com.core.data.file.model.FileData
import com.core.data.network.model.ApiResult

@SuppressWarnings("kotlin:S6517")
interface IDownloadManager {
    /**
     * Requests the download of a file from a given URL.
     *
     * This function initiates a download request for a file specified by its URL.
     * It allows specifying the desired file name, URL, file extension, and optional HTTP headers.
     * The function is designed to be used within a coroutine and suspends until the download request is completed or encounters an error.
     *
     * @param fileName The desired name for the downloaded file (excluding the extension).
     * @param url The URL from which to download the file.
     * @param extension The file extension to be used for the downloaded file. Defaults to "pdf".
     * @param headers Optional HTTP headers to be included in the download request. Defaults to an empty map.
     * @return An [ApiResult] containing either a [FileData] object representing the downloaded file or an error.
     *          - [ApiResult.Success]: Indicates a successful download, containing the [FileData].
     *          - [ApiResult.Error]: Indicates an error during the download, containing the error details.
     *
     * @see ApiResult
     * @see FileData
     *
     * @throws [Exception] if there are network issues, file access issues, or other errors during the download.
     */
    suspend fun requestDownload(
        fileName: String,
        url: String,
        extension: String = "pdf",
        headers: Map<String, String> = emptyMap(),
    ): ApiResult<FileData>
}
