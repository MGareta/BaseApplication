package com.core.data.download.api

import com.core.data.network.model.ApiResult
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Streaming
import retrofit2.http.Url

fun interface DownloadApiService {
    @GET
    @Streaming
    suspend fun downloadFile(@Url url: String, @HeaderMap headers: Map<String, String>): ApiResult<ResponseBody>
}
