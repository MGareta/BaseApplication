package com.core.data.activityResult.utils

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.core.data.activityResult.manager.IActivityResultManager

/**
 * Requests a single permission from the user.
 *
 * This function simplifies requesting a single permission using the [ActivityResultContracts.RequestPermission] contract.
 * It suspends until the user grants or denies the permission, and then returns a boolean indicating the result.
 *
 * @param permission The permission to request, e.g., `android.Manifest.permission.CAMERA`.
 * @return `true` if the permission was granted, `false` otherwise (including if the request was cancelled).
 *
 * @see ActivityResultContracts.RequestPermission
 * @see IActivityResultManager.requestResult
 *
 * Example usage:
 * ```kotlin
 * val permissionGranted = activityResultManager.requestPermission(android.Manifest.permission.CAMERA)
 * if (permissionGranted) {
 *     // Permission granted, proceed with camera-related operations.
 * } else {
 *     // Permission denied. Handle accordingly.
 * }
 * ```
 */
suspend fun IActivityResultManager.requestPermission(permission: String): Boolean = requestResult(
    contract = ActivityResultContracts.RequestPermission(),
    input = permission,
) ?: false

/**
 * Requests multiple permissions from the user.
 *
 * This function simplifies the process of requesting multiple permissions simultaneously.
 * It leverages the [ActivityResultContracts.RequestMultiplePermissions] contract to handle the
 * underlying activity result management.
 *
 * @param permission vararg of permissions to request.
 *        e.g.,  `Manifest.permission.CAMERA`, `Manifest.permission.ACCESS_FINE_LOCATION`.
 * @return A [Map] where keys are the requested permission strings and values are booleans
 *         indicating whether the permission was granted. Returns `null` if the request was canceled or
 *         encountered an error.
 *
 * @throws IllegalStateException if no Activity is set.
 *
 * Example Usage:
 * ```
 *  val result = activityResultManager.requestPermissions(
 *     Manifest.permission.CAMERA,
 *     Manifest.permission.ACCESS_FINE_LOCATION
 *  )
 *
 *  if (result != null) {
 *      if (result[Manifest.permission.CAMERA] == true) {
 *          // Camera permission granted.
 *      }
 *  }
 * ```
 *
 * @see [ActivityResultContracts.RequestMultiplePermissions]
 * @see [IActivityResultManager.requestResult]
 */
suspend fun IActivityResultManager.requestPermissions(vararg permission: String): Map<String, Boolean>? = requestResult(
    contract = ActivityResultContracts.RequestMultiplePermissions(),
    input = arrayOf(*permission),
)

/**
 * Captures a preview image using the device's camera and returns it as a Bitmap.
 *
 * This function utilizes the `TakePicturePreview` activity result contract.
 *
 * @return A [Bitmap] representing the captured preview image, or `null` if the operation was unsuccessful.
 *
 * @see ActivityResultContracts.TakePicturePreview
 * @see IActivityResultManager.requestResult
 */
suspend fun IActivityResultManager.takePicturePreview(): Bitmap? = requestResult(
    contract = ActivityResultContracts.TakePicturePreview(),
    input = null,
)

/**
 * Takes a picture and saves it to the specified destination [Uri].
 *
 * This function uses the [ActivityResultContracts.TakePicture] contract to capture an image
 * and save it to the provided [destination] URI. It's designed to be used within
 * an [IActivityResultManager] instance.
 *
 * @param destination The [Uri] where the captured image should be saved.
 * @return `true` if the picture was successfully taken and saved to the destination, `false` otherwise.
 *
 * @throws Exception If any error occurs during the process of taking the picture.
 */
suspend fun IActivityResultManager.takePicture(destination: Uri): Boolean = requestResult(
    contract = ActivityResultContracts.TakePicture(),
    input = destination,
) ?: false

/**
 * Captures a video and saves it to the specified destination URI.
 *
 * This function uses the `ActivityResultContracts.CaptureVideo` contract to initiate a video capture intent.
 * It then handles the result, returning `true` if the video was successfully captured and saved, and `false` otherwise.
 *
 * @param destination The URI where the captured video should be saved.
 *                    This should be a valid content URI where the application has write access.
 * @return `true` if the video was successfully captured and saved, `false` otherwise.
 *         Returns `false` also if the activity result is null.
 * @throws SecurityException If the application does not have the necessary permissions to write to the destination URI.
 * @throws IllegalArgumentException If the destination URI is invalid or cannot be used for writing.
 * @throws IllegalStateException If the activity result registry is not in a state where it can receive results.
 */
suspend fun IActivityResultManager.captureVideo(destination: Uri): Boolean = requestResult(
    contract = ActivityResultContracts.CaptureVideo(),
    input = destination,
) ?: false

/**
 * Suspending function to pick a contact from the device's contacts list.
 *
 * This function utilizes the [ActivityResultContracts.PickContact] contract to
 * initiate the contact selection process and retrieve the resulting contact URI.
 *
 * @return The URI of the selected contact, or null if the user cancelled the operation.
 *
 * @see [ActivityResultContracts.PickContact] for details on the contract.
 * @see [IActivityResultManager.requestResult] for the underlying result handling mechanism.
 *
 * @receiver [IActivityResultManager] The activity result manager to use for the request.
 */
suspend fun IActivityResultManager.pickContact(): Uri? = requestResult(
    contract = ActivityResultContracts.PickContact(),
    input = null,
)

/**
 * Launches an activity to allow the user to select a file or content item and returns the contentURI.
 *
 * This function utilizes the [ActivityResultContracts.GetContent] contract to initiate the content
 * selection process and retrieve the resulting content URI.
 *
 * @param mimeType The MIME type of the content to retrieve (e.g., "image/*", "video/*", "application/pdf", "*/*").
 *                 Using "*/*" will allow the user to select any file type.
 * @return The URI of the selected content, or null if the user cancelled the operation.
 *
 * @see [ActivityResultContracts.GetContent] for details on the contract.
 * @see [IActivityResultManager.requestResult] for the underlying result handling mechanism.
 *
 *  @receiver [IActivityResultManager] The activity result manager to use for the request.
 */
suspend fun IActivityResultManager.getContent(mimeType: String): Uri? = requestResult(
    contract = ActivityResultContracts.GetContent(),
    input = mimeType,
)

/**
 * Retrieves multiple content URIs from the system picker based on the specified MIME type.
 *
 * This function launches an activity to allow the user to select multiple files or content
 * items matching the given MIME type. It returns a list of URIs representing the selected
 * items, or an empty list if the user cancels the operation or no items are selected.
 *
 * @param mimeType The MIME type of the content to retrieve (e.g., "image/*", "video/*", "application/pdf", "*/*").
 *                 Using "*/*" will allow the user to select any file type.
 * @return A list of [Uri] representing the selected content items, or an empty list if no items are selected.
 * @throws Exception If there is an error launching or interacting with the system picker activity.
 * @sample
 * ```
 *  // Example usage to get multiple image URIs
 *  val imageUris: List<Uri> = activityResultManager.getMultipleContents("image/*")
 *  if(imageUris.isNotEmpty()){
 *    //Process the selected image uris
 *    imageUris.forEach{ uri ->
 *      // do something with the image uri
 *    }
 *  }
 *
 *  //Example usage to get multiple files of any type
 *  val filesUris: List<Uri> = activityResultManager.getMultipleContents("*/*")
 *   if(filesUris.isNotEmpty()){
 *    //Process the selected files uris
 *    filesUris.forEach{ uri ->
 *      // do something with the file uri
 *    }
 *  }
 * ```
 */
suspend fun IActivityResultManager.getMultipleContents(mimeType: String): List<Uri> = requestResult(
    contract = ActivityResultContracts.GetMultipleContents(),
    input = mimeType,
) ?: emptyList()

/**
 * Launches an activity to allow the user to select a document and returns the document URI.
 *
 * This function utilizes the [ActivityResultContracts.OpenDocument] contract to initiate the document
 * selection process and retrieve the resulting document URI.
 *
 * @param mimeType The MIME type of the content to retrieve (e.g., "image/*", "video/*", "application/pdf", "*/*").
 *                 Using "*/*" will allow the user to select any file type.
 * @return The URI of the selected document, or null if the user cancelled the operation.
 *
 * @see [ActivityResultContracts.OpenDocument] for details on the contract.
 * @see [IActivityResultManager.requestResult] for the underlying result handling mechanism.
 *
 * @receiver [IActivityResultManager] The activity result manager to use for the request.
 */
suspend fun IActivityResultManager.openDocument(mimeType: String): Uri? = requestResult(
    contract = ActivityResultContracts.OpenDocument(),
    input = arrayOf(mimeType),
)

/**
 * Retrieves multiple document URIs from the system picker based on the specified MIME types.
 *
 * This function launches an activity to allow the user to select multiple documents
 * matching the given MIME types. It returns a list of URIs representing the selected
 * documents, or an empty list if the user cancels the operation or no documents are selected.
 *
 * @param mimeType The MIME type of the content to retrieve (e.g., "image/*", "video/*", "application/pdf", "*/*").
 *                 Using "*/*" will allow the user to select any file type.
 * @return A list of [Uri] representing the selected documents, or an empty list if no documents are selected.
 * @throws Exception If there is an error launching or interacting with the system picker activity.
 *
 * @see [ActivityResultContracts.OpenMultipleDocuments] for details on the contract.
 * @see [IActivityResultManager.requestResult] for the underlying result handling mechanism.
 *
 * @receiver [IActivityResultManager] The activity result manager to use for the request.
*/
suspend fun IActivityResultManager.openMultipleDocuments(mimeTypes: Array<String>): List<Uri> = requestResult(
    contract = ActivityResultContracts.OpenMultipleDocuments(),
    input = mimeTypes,
) ?: emptyList()

/**
 * Opens a document tree picker using the [ActivityResultContracts.OpenDocumentTree] contract.
 *
 * This function suspends until the user has selected a directory and the result is returned.
 *
 * @param startingLocation An optional [Uri] representing the initial location that the picker should open to. If not specified, the picker will open to the default location.
 * @return The [Uri] representing the selected directory, or `null` if the user canceled the operation.
 *
 * @see ActivityResultContracts.OpenDocumentTree
 * @see IActivityResultManager.requestResult
 */
suspend fun IActivityResultManager.openDocumentTree(startingLocation: Uri? = null): Uri? = requestResult(
    contract = ActivityResultContracts.OpenDocumentTree(),
    input = startingLocation,
)

/**
 * Creates a new document using the system's document picker.
 *
 * This function launches an activity to prompt the user to choose a location and filename for a new document.
 * It utilizes the `ActivityResultContracts.CreateDocument` contract to handle the activity result.
 *
 * @param fileName The suggested filename for the new document. The user can change this.
 * @param mimeType The MIME type of the document to be created (e.g., "text/plain", "application/pdf").
 * @return The [Uri] representing the newly created document, or `null` if the user cancelled the operation or an error occurred.
 *
 * @see ActivityResultContracts.CreateDocument
 * @see IActivityResultManager.requestResult
 * @since 1.0.0
 */
suspend fun IActivityResultManager.createDocument(fileName: String, mimeType: String): Uri? = requestResult(
    contract = ActivityResultContracts.CreateDocument(mimeType = mimeType),
    input = fileName,
)

/**
 * Starts an intent sender for a given [IntentSenderRequest] and returns the result.
 *
 * This function leverages the AndroidX Activity Result API to handle launching an intent sender
 * (e.g., for requesting user consent, payments, etc.) and receiving the result.
 * It's a suspend function, so it should be called within a coroutine or another suspend function.
 *
 * @param intentSenderRequest The [IntentSenderRequest] containing the details of the intent sender to be launched.
 *        This includes the [android.content.IntentSender] itself, any fill-in intents, and any flags.
 * @return An [ActivityResult] containing the result of the launched intent sender, or `null` if the request was not completed.
 *         The [ActivityResult] will contain a `resultCode` (e.g., `Activity.RESULT_OK`, `Activity.RESULT_CANCELED`) and
 *         optionally an `intent` containing any data returned by the launched activity.
 *
 * @throws IllegalStateException If the activity result launcher is not initialized. This will happen if you attempt
 *         to call this function before the fragment or activity's `onCreate` has been called or before registerForActivityResult has been called.
 *
 * @sample
 * ```kotlin
 * // Example usage within a coroutine:
 * lifecycleScope.launch {
 *     val intentSender = // ... create your IntentSender instance ...
 *     val request = IntentSenderRequest.Builder(intentSender).build()
 *     val result = activityResultManager.startIntentSender(request)
 *     if (result?.resultCode == Activity.RESULT_OK) {
 *         // Handle successful result
 *         val data = result.data
 *         // ... process the returned data ...
 *     } else {
 *         // Handle cancelled or failed result
 *     }
 * }
 * ```
 */
suspend fun IActivityResultManager.startIntentSender(intentSenderRequest: IntentSenderRequest): ActivityResult? =
    requestResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        input = intentSenderRequest,
    )

/**
 * Starts an activity for a result using the provided intent.
 *
 * This function leverages the `ActivityResultContracts.StartActivityForResult` contract
 * to launch an activity and retrieve the result when it finishes. It's a suspending
 * function, meaning it will pause execution until the result is available.
 *
 * @param intent The intent to start the activity with. This intent should contain
 *               all necessary data for the target activity.
 * @return An [ActivityResult] object containing the result code and data
 *         from the launched activity. Returns `null` if the activity was canceled
 *         or if no result is available. The `ActivityResult` object will contain:
 *          - `resultCode`: An integer result code returned by the child activity through its
 *          setResult(). This is typically RESULT_OK or RESULT_CANCELED.
 *          - `data`: An Intent, which can return result data to the caller (various data can be
 *          attached to Intent "extra").
 *
 * @throws IllegalStateException If called outside of a context that supports
 *                                  activity result handling (e.g., if not called
 *                                  within a Fragment or Activity lifecycle).
 * @throws Exception if any error occurred during the process.
 *
 * @see ActivityResultContracts.StartActivityForResult
 * @see IActivityResultManager.requestResult
 * @see ActivityResult
 * @see Intent
 * @see android.app.Activity.RESULT_OK
 * @see android.app.Activity.RESULT_CANCELED
 */
suspend fun IActivityResultManager.startActivityForResult(intent: Intent): ActivityResult? = requestResult(
    contract = ActivityResultContracts.StartActivityForResult(),
    input = intent,
)
