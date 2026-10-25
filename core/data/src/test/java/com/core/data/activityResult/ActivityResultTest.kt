package com.core.data.activityResult

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.core.data.HiltRobolectricBaseTest
import com.core.data.activityResult.manager.IActivityResultManager
import com.core.data.activityResult.utils.captureVideo
import com.core.data.activityResult.utils.createDocument
import com.core.data.activityResult.utils.getContent
import com.core.data.activityResult.utils.getMultipleContents
import com.core.data.activityResult.utils.openDocument
import com.core.data.activityResult.utils.openDocumentTree
import com.core.data.activityResult.utils.openMultipleDocuments
import com.core.data.activityResult.utils.pickContact
import com.core.data.activityResult.utils.requestPermission
import com.core.data.activityResult.utils.requestPermissions
import com.core.data.activityResult.utils.startActivityForResult
import com.core.data.activityResult.utils.startIntentSender
import com.core.data.activityResult.utils.takePicture
import com.core.data.activityResult.utils.takePicturePreview
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@HiltAndroidTest
class ActivityResultTest : HiltRobolectricBaseTest() {

    private val activityResultManager: IActivityResultManager = mockk()

    override fun initDependencies() = Unit

    @Test
    fun requestPermissionUsesRequestPermissionContract() = test {
        val permission = Manifest.permission.CAMERA

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.RequestPermission>(),
                input = permission
            )
        } returns true

        assertTrue(activityResultManager.requestPermission(permission = permission))
    }

    @Test
    fun requestPermissionReturnsFalseWhenResultIsNull() = test {
        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.RequestPermission>(),
                input = Manifest.permission.CAMERA
            )
        } returns null

        assertFalse(activityResultManager.requestPermission(permission = Manifest.permission.CAMERA))
    }

    @Test
    fun requestPermissionsPassesAllPermissionNames() = test {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            "permission.LOCATION"
        )

        val result = mapOf(
            permissions[0] to true,
            permissions[1] to false
        )

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.RequestMultiplePermissions>(),
                input = match { it.contentEquals(other = permissions) }
            )
        } returns result

        assertEquals(result, activityResultManager.requestPermissions(
            *permissions
        ))
    }

    @Test
    fun takePicturePreviewUsesTakePicturePreviewContract() = test {
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.TakePicturePreview>(),
                input = null
            )
        } returns bitmap

        assertEquals(bitmap, activityResultManager.takePicturePreview())
    }

    @Test
    fun pictureAndVideoCaptureReturnFalseWhenResultIsNull() = test {
        val destination = Uri.parse("content://test/media")

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.TakePicture>(),
                input = destination
            )
        } returns null

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.CaptureVideo>(),
                input = destination
            )
        } returns null

        assertFalse(activityResultManager.takePicture(destination))
        assertFalse(activityResultManager.captureVideo(destination))
    }

    @Test
    fun pickContactReturnsSelectedContactUri() = test {
        val contact = Uri.parse("content://contacts/1")

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.PickContact>(),
                input = null
            )
        } returns contact

        assertEquals(contact, activityResultManager.pickContact())
    }

    @Test
    fun getContentPassesMimeTypeAndReturnsUri() = test {
        val mimeType = "image/*"
        val content = Uri.parse("content://files/image")

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.GetContent>(),
                input = mimeType
            )
        } returns content

        assertEquals(content, activityResultManager.getContent(mimeType))
    }

    @Test
    fun getMultipleContentsReturnsEmptyListWhenResultIsNull() = test {
        val mimeType = "image/*"

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.GetMultipleContents>(),
                input = mimeType
            )
        } returns null

        assertTrue(activityResultManager.getMultipleContents(mimeType).isEmpty())
    }

    @Test
    fun openDocumentPassesMimeTypesAndReturnsUri() = test {
        val mimeTypes = arrayOf("application/pdf")
        val document = Uri.parse("content://files/document")

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.OpenDocument>(),
                input = match { it.contentEquals(mimeTypes) }
            )
        } returns document

        assertEquals(document, activityResultManager.openDocument(
            mimeType = mimeTypes.single()
        ))
    }

    @Test
    fun openMultipleDocumentsReturnsEmptyListWhenResultIsNull() = test {
        val mimeTypes = arrayOf("application/pdf", "text/plain")

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.OpenMultipleDocuments>(),
                input = match { it.contentEquals(mimeTypes) }
            )
        } returns null

        assertTrue(activityResultManager.openMultipleDocuments(
            mimeTypes = mimeTypes
        ).isEmpty())
    }

    @Test
    fun openDocumentTreePassesOptionalStartingLocation() = test {
        val startingLocation = Uri.parse("content://files/root")
        val selectedLocation = Uri.parse("content://files/selected")

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.OpenDocumentTree>(),
                input = startingLocation
            )
        } returns selectedLocation

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.OpenDocumentTree>(),
                input = null
            )
        } returns selectedLocation

        assertEquals(selectedLocation, activityResultManager.openDocumentTree(
            startingLocation = startingLocation
        ))
        assertEquals(selectedLocation, activityResultManager.openDocumentTree())
    }

    @Test
    fun createDocumentPassesFilenameAndMimeType() = test {
        val fileName = "report.pdf"
        val mimeType = "application/pdf"
        val document = Uri.parse("content://files/report")

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.CreateDocument>(),
                input = fileName
            )
        } returns document

        assertEquals(document, activityResultManager.createDocument(
            fileName = fileName,
            mimeType = mimeType
        ))
    }

    @Test
    fun startIntentSenderPassesRequestAndReturnsActivityResult() = test {
        val request = mockk<IntentSenderRequest>()
        val result = ActivityResult(Activity.RESULT_OK, Intent("complete"))

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.StartIntentSenderForResult>(),
                input = request
            )
        } returns result

        assertEquals(result, activityResultManager.startIntentSender(
            intentSenderRequest = request
        ))
    }

    @Test
    fun startActivityForResultPassesIntentAndReturnsActivityResult() = test {
        val intent = Intent("test.action")
        val result = ActivityResult(Activity.RESULT_OK, Intent("complete"))

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.StartActivityForResult>(),
                input = intent
            )
        } returns result

        assertEquals(result, activityResultManager.startActivityForResult(
            intent = intent
        ))
    }
}
