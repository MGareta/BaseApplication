---
type: package reference
title: File storage
description: Reference for com.core.data.file. Covers IFileManager.saveFile and findFile, which store and look up files in the app's internal filesDir (PDFs under documents/), handle legacy storage permission requests, and return FileProvider content URIs via the manifest-declared provider and provider_paths.xml.
tags: [core-data, files, fileprovider, permissions, hilt]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T12:11:14.549Z
sources:
  - id: openwiki-source-a56635df0f53aef410e3990b
    resource: repo://core/data/src/main/AndroidManifest.xml
  - id: openwiki-source-6f6b64d1a60f81f60fea9ff7
    resource: repo://core/data/src/main/java/com/core/data/file/di/FileModule.kt
  - id: openwiki-source-bbaebea14c4b6d62af4a2d72
    resource: repo://core/data/src/main/java/com/core/data/file/manager/FileManagerImpl.kt
  - id: openwiki-source-9d1811ac3b83ccd28b767d1b
    resource: repo://core/data/src/main/res/xml/provider_paths.xml
generated: { by: "claude-code", at: "2026-10-26T12:11:14.549Z" }
---

# File storage (`com.core.data.file`)

## Purpose and when to use it

`IFileManager` saves byte arrays to and finds files in **app-internal storage** (`context.filesDir`), and returns each file as a
`FileData` with a shareable `content://` URI produced by `androidx.core.content.FileProvider`.

Use it to persist downloaded documents ([downloads](download.md) uses it) and to produce URIs you can pass to other apps
(`Intent.ACTION_VIEW`, share sheets, `takePicture` destinations).
Don't use it for shared/public storage (MediaStore/SAF) or for structured data (use cache, DataStore or Room).

## Package layout

```
com/core/data/file/
├── di/FileModule.kt
├── manager/
│   ├── IFileManager.kt
│   └── FileManagerImpl.kt
└── model/FileData.kt
```

Related resources in core/data:
- `src/main/AndroidManifest.xml` declares `androidx.core.content.FileProvider` with `android:authorities="${applicationId}"`,
  `exported="false"`, `grantUriPermissions="true"`, and meta-data `android.support.FILE_PROVIDER_PATHS` → `@xml/provider_paths`.
  It also declares `WRITE_EXTERNAL_STORAGE` and `READ_EXTERNAL_STORAGE` (both `maxSdkVersion="32"`) and sets `requestLegacyExternalStorage="true"`.
- `src/main/res/xml/provider_paths.xml` contains `<files-path name="files" path="/" />`, which exposes the whole `filesDir` tree through the provider.

## Dependency injection

`FileModule` (`object`, `@InstallIn(SingletonComponent::class)`):
`@Singleton @Provides fun providesFileManager(@ApplicationContext context: Context, permissionManager: IPermissionManager): IFileManager = FileManagerImpl(context, permissionManager)`.

```kotlin
class ExportReportUseCase @Inject constructor(private val fileManager: IFileManager)
```

## Type reference

### `model/FileData`
`class FileData(val fileName: String, val uri: Uri, val mimeType: String?)`. It's a plain class (not a data class), so it has no
`equals`/`copy`. `fileName` is the base name the caller passed (without extension). `uri` is the `FileProvider` content URI.

### `manager/IFileManager`
```kotlin
suspend fun findFile(fileName: String, mimeType: String): ApiResult<FileData>
suspend fun saveFile(data: ByteArray, fileName: String, mimeType: String): ApiResult<FileData>
```

### `manager/FileManagerImpl`
Constructor: `(@param:ApplicationContext context: Context, permissionManager: IPermissionManager)`.

**Permission step (both methods).** `findFile` checks `READ_EXTERNAL_STORAGE` and `saveFile` checks `WRITE_EXTERNAL_STORAGE`:
1. If `permissionManager.hasPermission(p)`, the status is `Granted`.
2. Otherwise it calls `permissionManager.requestPermission(p)`. If the result isn't `Granted` **and** `SDK_INT >= 33 (TIRAMISU)`, it's treated as `Granted`
   (these permissions don't exist or can't be granted from API 33 on). Otherwise it uses the result.
3. Only `PermissionStatus.Denied` aborts, with `ApiResult.Exception(IllegalStateException("Permission denied"))`. `ShowRationale` proceeds.

**Directory rule.** The subdirectory is `"documents"` when `mimeType == "application/pdf"`, otherwise `""` (the root of `filesDir`).

**`findFile(fileName, mimeType)`**
- Lists `File(filesDir, subdirectory)` and picks the first file whose `nameWithoutExtension == fileName`.
- No match, or a missing directory (`listFiles` returns `null`) → `ApiResult.NoContent()`.
- Match → `Success(FileData(fileName, FileProvider.getUriForFile(context, context.packageName, file), mimeType))`.

**`saveFile(data, fileName, mimeType)`**
- Creates the subdirectory with `mkdir()` if it's missing. On failure it returns `ApiResult.Exception(IllegalStateException("Failed to create directory"))`.
- Extension: `MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)`. The file name is `"$fileName.$ext"`, or just `fileName` when it's unknown.
- Writes the bytes (it overwrites an existing file with the same name) and returns `Success(FileData(...))` with the provider URI.
- I/O exceptions **aren't caught** and propagate to the caller. `DownloadManagerImpl` wraps them through `ApiResult.map`.

All work runs on the calling dispatcher. Use `Dispatchers.IO` for large files.

## Data flow

<!-- openwiki: mermaid parse failed and this diagram was converted to a text fence so it does not break rendering. Fix the diagram source and restore the mermaid fence. Parser error: Heuristic: an unescaped angle bracket inside a label breaks rendering; rephrase the label. -->
```text
flowchart TD
    C[saveFile bytes, name, mime] --> P{hasPermission WRITE_EXTERNAL_STORAGE?}
    P -- no --> RQ[requestPermission via IPermissionManager]
    RQ --> T{API >= 33?}
    T -- yes --> OK[treat as Granted]
    T -- no --> D{Denied?}
    D -- yes --> EX[ApiResult.Exception Permission denied]
    D -- no --> OK
    P -- yes --> OK
    OK --> DIR[filesDir / documents if PDF]
    DIR --> W[write name.ext]
    W --> URI[FileProvider.getUriForFile authority = packageName]
    URI --> S[ApiResult.Success FileData]
```

## Usage examples

```kotlin
val saved = fileManager.saveFile(data = pdfBytes, fileName = "statement_2026_10", mimeType = "application/pdf")
saved.onSuccess { file ->
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(file.uri, file.mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    activityResultManager.startActivityForResult(intent)
}

when (val found = fileManager.findFile(fileName = "statement_2026_10", mimeType = "application/pdf")) {
    is ApiResult.Success -> show(found.data.uri)
    is ApiResult.NoContent -> download()
    else -> Unit
}
```

## Extension guide

- **New MIME-specific folder:** extend the `when (mimeType)` in **both** `findFile` and `saveFile` (they're duplicated),
  for example `"image/jpeg" -> "images"`. Paths stay under `filesDir`, so `provider_paths.xml` (`path="/"`) already covers them.
- **Delete/list APIs:** add them to `IFileManager` and implement them in `FileManagerImpl` with the same directory rule, then add tests to `FileManagerTest`.

## Gotchas and constraints

- **The FileProvider authority is `context.packageName`**, while the manifest declares `${applicationId}`. These are equal
  in a normal app, but they differ if the app's namespace and applicationId diverge and code calls `getUriForFile` with another authority.
  Only one provider with authority `${applicationId}` can exist, so an app module that declares its own `FileProvider` with the same authority will clash in the manifest merge.
- **Permissions are unnecessary for `filesDir`** but are still requested on API < 33. That shows a system dialog and requires a
  foreground `FragmentActivity` (through the activity result manager). If no activity is available the request resolves to `Denied`
  (see [permission](permission.md)) and the call fails.
- `findFile` matches by base name only: `report.pdf` and `report.txt` in the same directory are indistinguishable.
- Non-PDF files all share the `filesDir` root.

## Testing

`FileManagerTest` (`@HiltAndroidTest`, `BaseTest`) grants storage permissions with Robolectric's `shadowOf(Application).grantPermissions`
and covers: saving a PDF (extension and contents), finding a saved file, and `NoContent` for a missing file.
`clearFileProviderCache()` (test `utils/Utils.kt`) resets `FileProvider`'s static path cache between tests.

## Related pages

- [Downloads](download.md)
- [Permissions](permission.md)
- [Activity results](activity-result.md)
- [Overview](overview.md) 
- (manifest/resources)
- [Testing](testing.md)
