package com.core.data.activityResult.utils

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.core.data.activityResult.manager.IActivityResultManager

suspend fun IActivityResultManager.requestPermission(
    permission: String
): Boolean = requestResult(
    contract = ActivityResultContracts.RequestPermission(),
    input = permission
) ?: false

suspend fun IActivityResultManager.requestPermissions(
    vararg permission: String
): Map<String, Boolean>? = requestResult(
    contract = ActivityResultContracts.RequestMultiplePermissions(),
    input = arrayOf(*permission)
)

suspend fun IActivityResultManager.takePicturePreview(): Bitmap? = requestResult(
    contract = ActivityResultContracts.TakePicturePreview(),
    input = null
)

suspend fun IActivityResultManager.takePicture(
    destination: Uri
): Boolean = requestResult(
    contract = ActivityResultContracts.TakePicture(),
    input = destination
) ?: false

suspend fun IActivityResultManager.captureVideo(
    destination: Uri
): Boolean = requestResult(
    contract = ActivityResultContracts.CaptureVideo(),
    input = destination
) ?: false

suspend fun IActivityResultManager.pickContact(): Uri? = requestResult(
    contract = ActivityResultContracts.PickContact(),
    input = null
)

suspend fun IActivityResultManager.getContent(
    mimeType: String
): Uri? = requestResult(
    contract = ActivityResultContracts.GetContent(),
    input = mimeType
)

suspend fun IActivityResultManager.getMultipleContents(
    mimeType: String
): List<Uri> = requestResult(
    contract = ActivityResultContracts.GetMultipleContents(),
    input = mimeType
) ?: emptyList()

suspend fun IActivityResultManager.openDocument(
    mimeType: String
): Uri? = requestResult(
    contract = ActivityResultContracts.OpenDocument(),
    input = arrayOf(mimeType)
)

suspend fun IActivityResultManager.openMultipleDocuments(
    mimeTypes: Array<String>
): List<Uri> = requestResult(
    contract = ActivityResultContracts.OpenMultipleDocuments(),
    input = mimeTypes
) ?: emptyList()

suspend fun IActivityResultManager.openDocumentTree(
    startingLocation: Uri? = null
): Uri? = requestResult(
    contract = ActivityResultContracts.OpenDocumentTree(),
    input = startingLocation
)

suspend fun IActivityResultManager.createDocument(
    fileName: String,
    mimeType: String
): Uri? = requestResult(
    contract = ActivityResultContracts.CreateDocument(mimeType = mimeType),
    input = fileName
)

suspend fun IActivityResultManager.startIntentSender(
    intentSenderRequest: IntentSenderRequest
): ActivityResult? = requestResult(
    contract = ActivityResultContracts.StartIntentSenderForResult(),
    input = intentSenderRequest
)

suspend fun IActivityResultManager.startActivityForResult(
    intent: Intent
): ActivityResult? = requestResult(
    contract = ActivityResultContracts.StartActivityForResult(),
    input = intent
)
