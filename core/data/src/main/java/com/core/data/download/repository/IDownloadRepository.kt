package com.core.data.download.repository

import com.core.data.network.model.ApiResult
import okhttp3.ResponseBody

fun interface IDownloadRepository {

    suspend fun downloadFile(
        url: String,
        headers: Map<String, String>
    ): ApiResult<ResponseBody>
}
