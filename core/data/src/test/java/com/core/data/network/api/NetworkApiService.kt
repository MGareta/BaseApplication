package com.core.data.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

internal interface NetworkApiService {

    @GET("response")
    fun response(): Call<ResponseBody>
}