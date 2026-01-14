package com.core.data.download.manager

import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.core.data.download.repository.IDownloadRepository
import com.core.data.file.manager.IFileManager
import com.core.data.file.model.FileData
import com.core.data.network.model.ApiResult
import com.core.data.network.utils.map
import javax.inject.Inject

class DownloadManagerImpl @Inject constructor(
    private val repository: IDownloadRepository,
    private val fileManager: IFileManager,
) : IDownloadManager {
    /**
     * **How it works:**
     * This function first checks if a file with the specified name already exists locally.
     * If it exists, it returns the local file information directly.
     * Otherwise, it downloads the file from the provided URL, determines its MIME type,
     * and saves it locally using the provided or inferred file name and extension.
     */
    override suspend fun requestDownload(
        fileName: String,
        url: String,
        extension: String,
        headers: Map<String, String>,
    ): ApiResult<FileData> {
        val uri = url.toUri()

        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString()).takeIf {
            it.isNotEmpty()
        } ?: extension

        val fileMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)

        val localFile = fileManager.findFile(
            fileName = fileName,
            mimeType = fileMimeType ?: "",
        )
        if (localFile is ApiResult.Success) {
            return localFile
        }

        return repository.downloadFile(url = url, headers = headers).map {
            fileManager.saveFile(
                data = it.byteStream().readBytes(),
                fileName = fileName,
                mimeType = fileMimeType ?: "",
            )
        }
    }
}
