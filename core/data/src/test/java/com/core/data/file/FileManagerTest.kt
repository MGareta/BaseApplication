package com.core.data.file

import android.Manifest
import android.app.Application
import com.core.data.BaseTest
import com.core.data.file.manager.IFileManager
import com.core.data.network.model.ApiResult
import com.core.data.utils.clearFileProviderCache
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.Shadows.shadowOf
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltAndroidTest
class FileManagerTest : BaseTest() {

    @Inject
    lateinit var fileManager: IFileManager

    private val uniqueFileName: String = "file_${UUID.randomUUID()}"

    override fun initDependencies() {
        shadowOf(context as Application)
            .grantPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

        clearFileProviderCache()
    }

    @Test
    fun savesFileWithPdfExtensionAndContents() = test {
        val fileBytes = byteArrayOf(1, 2, 3)

        val result = fileManager.saveFile(
            data = fileBytes,
            fileName = uniqueFileName,
            mimeType = "application/pdf"
        )

        assertTrue(result is ApiResult.Success)

        val fileData = (result as ApiResult.Success).data
        assertEquals(uniqueFileName, fileData.fileName)
        assertEquals("application/pdf", fileData.mimeType)
        assertArrayEquals(
            fileBytes,
            File(context.filesDir, "documents/$uniqueFileName.pdf").readBytes()
        )
    }

    @Test
    fun findsSavedFile() = test {
        val saveResult = fileManager.saveFile(
            data = byteArrayOf(1, 2, 3),
            fileName = uniqueFileName,
            mimeType = "application/pdf"
        )

        assertTrue(saveResult is ApiResult.Success)

        val result = fileManager.findFile(
            fileName = uniqueFileName,
            mimeType = "application/pdf"
        )

        assertTrue(result is ApiResult.Success)

        val fileData = (result as ApiResult.Success).data
        assertEquals(uniqueFileName, fileData.fileName)
        assertEquals("application/pdf", fileData.mimeType)
        assertEquals("content", fileData.uri.scheme)
    }

    @Test
    fun returnsNoContentWhenFileDoesNotExist() = test {
        val result = fileManager.findFile(
            fileName = uniqueFileName,
            mimeType = "application/pdf"
        )

        assertTrue(result is ApiResult.NoContent)
    }
}
