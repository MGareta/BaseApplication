package com.core.data.download.manager

import com.core.data.file.model.FileData
import com.core.data.network.model.ApiResult

interface IDownloadManager {

    suspend fun requestDownload(
        fileName: String,
        url: String,
        extension: String = "pdf",
        headers: Map<String, String> = emptyMap(),
    ): ApiResult<FileData>
}
