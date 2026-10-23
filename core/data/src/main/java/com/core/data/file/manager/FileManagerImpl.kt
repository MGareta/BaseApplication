package com.core.data.file.manager

import android.Manifest
import android.content.Context
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.core.data.file.model.FileData
import com.core.data.network.model.ApiResult
import com.core.data.permission.manager.IPermissionManager
import com.core.data.permission.model.PermissionStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File

class FileManagerImpl(
    @param:ApplicationContext private val context: Context,
    private val permissionManager: IPermissionManager
) : IFileManager {

    override suspend fun findFile(
        fileName: String,
        mimeType: String
    ): ApiResult<FileData> {
        val permissionStatus = if (!permissionManager.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val permissionResult = permissionManager.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)

            if (permissionResult != PermissionStatus.Granted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionStatus.Granted
            } else {
                permissionResult
            }
        } else {
            PermissionStatus.Granted
        }

        if (permissionStatus == PermissionStatus.Denied) {
            return ApiResult.Exception(IllegalStateException("Permission denied"))
        }

        val subdirectory = when (mimeType) {
            "application/pdf" -> "documents"
            else -> ""
        }

        val inputDirectory = File(
            context.filesDir,
            subdirectory,
        )

        val file = inputDirectory
            .listFiles { _, name ->
                File(name).nameWithoutExtension == fileName
            }?.firstOrNull() ?: return ApiResult.NoContent()

        return ApiResult.Success(
            FileData(
                fileName = fileName,
                uri = FileProvider.getUriForFile(
                    context,
                    context.packageName,
                    file,
                ),
                mimeType = mimeType,
            )
        )
    }

    override suspend fun saveFile(
        data: ByteArray,
        fileName: String,
        mimeType: String
    ): ApiResult<FileData> {
        val permissionStatus = if (!permissionManager.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val permissionResult = permissionManager.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permissionResult != PermissionStatus.Granted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionStatus.Granted
            } else {
                permissionResult
            }
        } else {
            PermissionStatus.Granted
        }

        if (permissionStatus == PermissionStatus.Denied) {
            return ApiResult.Exception(IllegalStateException("Permission denied"))
        }

        val subdirectory = when (mimeType) {
            "application/pdf" -> "documents"
            else -> ""
        }

        val outputDirectory = File(
            context.filesDir,
            subdirectory,
        )

        if (!outputDirectory.exists()) {
            val isDirectoryCreated = outputDirectory.mkdir()
            if (!isDirectoryCreated) {
                return ApiResult.Exception(IllegalStateException("Failed to create directory"))
            }
        }

        val documentBytes = BufferedInputStream(ByteArrayInputStream(data))
        val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        val outputFile = File(
            outputDirectory,
            if (fileExtension != null) {
                "$fileName.$fileExtension"
            } else {
                fileName
            }
        )

        documentBytes.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return ApiResult.Success(
            data = FileData(
                fileName = fileName,
                uri = FileProvider.getUriForFile(
                    context,
                    context.packageName,
                    outputFile,
                ),
                mimeType = mimeType,
            )
        )
    }
}
