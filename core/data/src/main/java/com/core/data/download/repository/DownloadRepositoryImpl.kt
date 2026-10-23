package com.core.data.download.repository

import com.core.data.download.api.DownloadApiService
import com.core.data.network.model.ApiResult
import okhttp3.ResponseBody

class DownloadRepositoryImpl(
    private val apiService: DownloadApiService
) : IDownloadRepository {

    override suspend fun downloadFile(
        url: String,
        headers: Map<String, String>
    ): ApiResult<ResponseBody> = apiService.downloadFile(
        url = url,
        headers = headers
    )
}
