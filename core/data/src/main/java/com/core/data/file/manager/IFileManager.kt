package com.core.data.file.manager

import com.core.data.file.model.FileData
import com.core.data.network.model.ApiResult

interface IFileManager {
    /**
     * Finds a file with the given file name.
     *
     * This function searches for a file based on the provided `fileName`.
     * It returns an [ApiResult] which can be either successful with [FileData] containing
     * the file's information, or an error indicating the reason for failure.
     *
     * @param fileName The name of the file to search for.
     * @param mimeType The MIME type of the file (e.g., "application/pdf",
     *                 "image/jpeg"). While this parameter is accepted, it's primarily
     *                 for informational purposes or potential future use. The file
     *                 is saved based on the filename extension, not the MIME type.
     * @return An [ApiResult] containing either:
     *   - [FileData]: If the file is found, this contains the file's data.
     *   - [ApiError]: If an error occurs during the search (e.g., file not found, network error).
     * @throws [Exception] if an unexpected error occurs during the operation.
     */
    suspend fun findFile(fileName: String, mimeType: String): ApiResult<FileData>

    /**
     * Saves a file to the local file system.
     *
     * This function writes the provided file data (as a byte array) to a file
     * on the device's local storage. It allows specifying the filename and MIME type
     * for informational purposes, but the MIME type doesn't affect the actual file
     * saving process.
     *
     * @param data The byte array representing the file's content.
     * @param fileName The desired filename for the file. This should include the
     *                 file extension (e.g., "file.pdf").
     * @param mimeType The MIME type of the file (e.g., "application/pdf",
     *                 "image/jpeg"). While this parameter is accepted, it's primarily
     *                 for informational purposes or potential future use. The file
     *                 is saved based on the filename extension, not the MIME type.
     * @param directory The directory where the file should be saved. If not provided will be saved to the cache directory.
     * @return An [ApiResult] containing either:
     *         - [FileData] if the file was saved successfully. This includes
     *           information about the saved file, such as its path and filename.
     *         - An error indicating the reason for failure, such as insufficient storage
     *           space or permission issues.
     *
     * @throws Exception if any unrecoverable error occurs during the saving process,
     *                   such as invalid file path or issues with writing to the file.
     */
    suspend fun saveFile(data: ByteArray, fileName: String, mimeType: String): ApiResult<FileData>
}
