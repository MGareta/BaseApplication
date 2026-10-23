package com.core.data.file.manager

import com.core.data.file.model.FileData
import com.core.data.network.model.ApiResult

interface IFileManager {

    suspend fun findFile(
        fileName: String,
        mimeType: String
    ): ApiResult<FileData>

    suspend fun saveFile(
        data: ByteArray,
        fileName: String,
        mimeType: String
    ): ApiResult<FileData>
}
