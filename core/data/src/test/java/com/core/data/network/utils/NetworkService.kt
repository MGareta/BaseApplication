package com.core.data.network.utils

import com.core.data.network.model.ApiResult
import retrofit2.Call

internal interface NetworkService {
    fun result(): Call<ApiResult<String>>

    fun raw(): Call<String>
}