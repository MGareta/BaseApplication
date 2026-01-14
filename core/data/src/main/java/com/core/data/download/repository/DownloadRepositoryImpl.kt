package com.core.data.download.repository

import com.core.data.download.api.DownloadApiService
import com.core.data.network.model.ApiResult
import okhttp3.ResponseBody

class DownloadRepositoryImpl(private val apiService: DownloadApiService) : IDownloadRepository {
    /**
     * **How it works:**
     * This function initiates a network request to download a file from the given URL.
     * It supports sending custom headers with the request. The function operates
     * asynchronously and returns an [ApiResult] object that encapsulates either a
     * successful [ResponseBody] or an error state.
     */
    override suspend fun downloadFile(url: String, headers: Map<String, String>): ApiResult<ResponseBody> =
        apiService.downloadFile(url = url, headers = headers)
}
