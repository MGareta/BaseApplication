package com.core.data.download

import android.Manifest
import android.app.Application
import com.core.data.HiltRobolectricBaseTest
import com.core.data.download.api.DownloadApiService
import com.core.data.download.manager.IDownloadManager
import com.core.data.file.manager.IFileManager
import com.core.data.network.model.ApiResult
import com.core.data.utils.clearFileProviderCache
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.coVerify
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.Shadows.shadowOf
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltAndroidTest
class DownloadManagerTest : HiltRobolectricBaseTest() {

    @Inject
    lateinit var downloadApiService: DownloadApiService

    @Inject
    lateinit var fileManager: IFileManager

    @Inject
    lateinit var downloadManager: IDownloadManager

    private val uniqueFileName: String = "download_${UUID.randomUUID()}"

    override fun initDependencies() {
        shadowOf(context as Application)
            .grantPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

        clearFileProviderCache()
    }

    @Test
    fun returnsExistingFileWithoutDownloading() = test {
        val savedResult = fileManager.saveFile(
            data = byteArrayOf(1, 2, 3),
            fileName = uniqueFileName,
            mimeType = "application/pdf"
        )

        assertTrue(savedResult is ApiResult.Success)

        val result = downloadManager.requestDownload(
            fileName = uniqueFileName,
            url = "https://example.com/document.pdf"
        )

        assertTrue(result is ApiResult.Success)

        val fileData = (result as ApiResult.Success).data
        assertEquals(uniqueFileName, fileData.fileName)
        assertEquals("application/pdf", fileData.mimeType)

        coVerify(exactly = 0) {
            downloadApiService.downloadFile(
                url = any(),
                headers = any()
            )
        }
    }

    @Test
    fun downloadsAndSavesResponseUsingUrlExtension() = test {
        val responseBytes = byteArrayOf(1, 2, 3)
        val headers = mapOf("Authorization" to "Bearer token")

        coEvery {
            downloadApiService.downloadFile(
                url = "https://example.com/document.pdf",
                headers = headers
            )
        } returns ApiResult.Success(responseBytes.toResponseBody())

        val result = downloadManager.requestDownload(
            fileName = uniqueFileName,
            url = "https://example.com/document.pdf",
            headers = headers
        )

        assertTrue(result is ApiResult.Success)

        val fileData = (result as ApiResult.Success).data
        assertEquals(uniqueFileName, fileData.fileName)
        assertEquals("application/pdf", fileData.mimeType)

        coVerify(exactly = 1) {
            downloadApiService.downloadFile(
                url = "https://example.com/document.pdf",
                headers = headers
            )
        }

        assertArrayEquals(
            responseBytes,
            File(context.filesDir, "documents/$uniqueFileName.pdf").readBytes()
        )
    }

    @Test
    fun usesProvidedExtensionWhenUrlHasNone() = test {

        coEvery {
            downloadApiService.downloadFile(
                url = "https://example.com/document",
                headers = emptyMap()
            )
        } returns ApiResult.Success("text".toByteArray().toResponseBody())

        val result = downloadManager.requestDownload(
            fileName = uniqueFileName,
            url = "https://example.com/document",
            extension = "txt"
        )

        assertTrue(result is ApiResult.Success)
        assertEquals("text/plain", (result as ApiResult.Success).data.mimeType)
        assertArrayEquals(
            "text".toByteArray(),
            File(context.filesDir, "$uniqueFileName.txt").readBytes()
        )
    }

    @Test
    fun propagatesDownloadFailureWithoutSavingFile() = test {
        coEvery {
            downloadApiService.downloadFile(
                url = "https://example.com/document.pdf",
                headers = emptyMap()
            )
        } returns ApiResult.Error(httpCode = 500, response = "Server error")

        val result = downloadManager.requestDownload(
            fileName = uniqueFileName,
            url = "https://example.com/document.pdf"
        )

        assertTrue(result is ApiResult.Error)
        assertTrue(!File(context.filesDir, "documents/$uniqueFileName.pdf").exists())
    }
}
