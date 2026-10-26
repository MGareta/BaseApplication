---
type: api reference
title: Coroutines use case runner
description: Reference for CoroutinesUseCaseRunner, the interface BaseViewModel implements to run use cases. Covers withUseCaseScope for fire-and-forget parallel blocks with loading and error callbacks, withUseCaseScopeLazily for turning an ApiResult call into a StateFlow of UiResult, and withUseCaseScopePaging for building a Paging 3 flow from PagingResponse pages. Also covers the zipWithPreviousCount flow helper.
tags: [core-presentation, coroutines, viewmodel, paging, apiresult, uiresult]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T15:48:49.783Z
sources:
  - id: openwiki-source-2605241fb6d0ff529d3cf13d
    resource: repo://core/presentation/src/main/java/com/core/presentation/base/viewmodel/BaseViewModel.kt
  - id: openwiki-source-9ade9fd0b236966d699bbe38
    resource: repo://core/presentation/src/main/java/com/core/presentation/coroutines/CoroutinesUseCaseRunner.kt
  - id: openwiki-source-77b3a42313c3514d770f5043
    resource: repo://core/presentation/src/main/java/com/core/presentation/coroutines/Utils.kt
  - id: openwiki-source-0cf78a6d6374c91a5eb8c322
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation/model/utils/navigator/bottomSheet/BottomSheetHost.kt
generated: { by: "claude-code", at: "2026-10-26T15:48:49.783Z" }
---

# Coroutines use case runner (`com.core.presentation.coroutines`)

## Purpose and when to use it

`CoroutinesUseCaseRunner` standardizes how ViewModels call into the data layer, which returns core/data's
[`ApiResult`](../core-data/network.md). `BaseViewModel` implements it with `useCaseCoroutineScope = viewModelScope`, so every helper is available
inside any screen ViewModel.

| Helper | Use it when |
|---|---|
| `withUseCaseScope` | You want to run one or more suspend blocks (in parallel), toggle a loading flag, and update state yourself. |
| `withUseCaseScopeLazily` | A screen section is "load once and show the result": you get a `StateFlow<UiResult<T>>` to expose and collect. |
| `withUseCaseScopePaging` | Infinite lists: you get a `Flow<PagingData<ITEM>>` from a page-loading block. |

## Package layout

```
com/core/presentation/coroutines/
├── CoroutinesUseCaseRunner.kt
└── Utils.kt                    # Flow<List<NavBackStackEntry>>.zipWithPreviousCount()
```

## `CoroutinesUseCaseRunner`

```kotlin
interface CoroutinesUseCaseRunner {
    val useCaseCoroutineScope: CoroutineScope
    fun withUseCaseScope(...)
    fun <T> withUseCaseScopeLazily(...): StateFlow<UiResult<T>>
    fun <KEY : Any, ITEM : Any> withUseCaseScopePaging(...): Flow<PagingData<ITEM>>
}
```
All three are default methods. Only `useCaseCoroutineScope` must be provided.

### `withUseCaseScope`
```kotlin
fun withUseCaseScope(
    loadingUpdater: ((Boolean) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null,
    vararg block: (suspend () -> Unit),
)
```
Behaviour:
1. `useCaseCoroutineScope.launch { ... }` returns immediately (no `Job` is returned).
2. `loadingUpdater?.invoke(true)`.
3. Every `block` is launched as a **child coroutine in parallel**, and then `joinAll()` waits for them.
4. `catch (e: Exception)` calls `onError?.invoke(e)`.
5. `finally` calls `loadingUpdater?.invoke(false)` and then `onComplete?.invoke()`. This always runs, including after errors.

Calling it: `block` is the last, vararg parameter. Pass named callbacks plus `block = arrayOf(...)`, or pass the blocks as trailing positional arguments:
```kotlin
withUseCaseScope(
    loadingUpdater = { setState { copy(isLoading = it) } },
    onError = { setState { copy(error = it.message) } },
    block = arrayOf(
        suspend { getProfile().onSuccess { p -> setState { copy(profile = p) } } },
        suspend { getBalance().onSuccess { b -> setState { copy(balance = b) } } },
    ),
)
```

**Exception caveat:** because the blocks run in child `launch`es, an exception thrown inside a block fails the parent coroutine. `joinAll()` then
throws a `CancellationException` (the original error is its cause), which is what `onError` receives, and the original exception still propagates
to `viewModelScope`'s uncaught-exception handling, which **crashes the app**. In practice data-layer calls return `ApiResult` and don't throw. Keep
blocks exception-free (use `ApiResult` operators, or wrap risky code in `runCatching`) rather than relying on `onError`.

### `withUseCaseScopeLazily`
```kotlin
fun <T> withUseCaseScopeLazily(
    initialValue: UiResult<T> = UiResult.Loading(),
    onError: ((ApiResult.Error<T>) -> UiResult.Error<T>),
    onException: ((ApiResult.Exception<T>) -> UiResult.Error<T>),
    block: (suspend () -> ApiResult<T>),
): StateFlow<UiResult<T>>
```
It builds `flow { emit(map(block())) }.stateIn(useCaseCoroutineScope, SharingStarted.WhileSubscribed(), initialValue)`:

| `ApiResult` | `UiResult` |
|---|---|
| `Success(data)` | `UiResult.Success(data)` |
| `Error(httpCode, response)` | `onError(result)`. You build the `UiResult.Error(message)`. |
| `Exception(throwable)` | `onException(result)` |
| `NoContent` | `UiResult.NoContent()` |

Semantics:
- **Lazy:** `block` runs only when the first collector subscribes. With `WhileSubscribed()` (stop timeout 0), the upstream is cancelled when the last collector
  leaves, and **`block` runs again** on the next subscription (for example after a configuration change or returning to the screen). Meanwhile the last value is kept.
- Each call creates a **new** `StateFlow`. Assign it once to a ViewModel property. Never call it from a composable or on every recomposition.
- There's no retry or refresh API. To reload, expose a trigger and use `flatMapLatest`, or use `withUseCaseScope` + `setState`.

```kotlin
@HiltViewModel
class OffersViewModel @Inject constructor(getOffers: GetOffersUseCase) : BaseViewModel<...>() {
    val offers: StateFlow<UiResult<List<Offer>>> = withUseCaseScopeLazily(
        onError = { UiResult.Error(message = it.response ?: "HTTP ${it.httpCode}") },
        onException = { UiResult.Error(message = it.exception.message ?: "Unexpected error") },
        block = { getOffers() },
    )
}

// UI
when (val result = viewModel.offers.collectAsStateWithLifecycle().value) {
    is UiResult.Loading -> Loader()
    is UiResult.Success -> OffersList(result.data)
    is UiResult.Error -> ErrorView(result.message)
    is UiResult.NoContent -> EmptyView()
}
```
Note: a property initializer like this runs during ViewModel construction, after `BaseViewModel`'s own initialization, which is fine.

### `withUseCaseScopePaging`
```kotlin
fun <KEY : Any, ITEM : Any> withUseCaseScopePaging(
    pageSize: Int,
    onError: ((ApiResult.Error<PagingResponse<KEY, ITEM>>) -> Unit)? = null,
    onException: ((ApiResult.Exception<PagingResponse<KEY, ITEM>>) -> Unit)? = null,
    calculateNextPage: (page: KEY?) -> KEY? = { it },
    keyReuseSupported: Boolean = false,
    block: (suspend (page: Any?) -> ApiResult<PagingResponse<KEY, ITEM>>),
): Flow<PagingData<ITEM>>
```
It builds `Pager(PagingConfig(pageSize)) { PagingSource }.flow.cachedIn(useCaseCoroutineScope)`. The anonymous `PagingSource<KEY, ITEM>`:

| `block(params.key)` result | `LoadResult` |
|---|---|
| `Success(PagingResponse(pageId, data))` | `Page(data = data, prevKey = params.key, nextKey = calculateNextPage(pageId))` |
| `NoContent` | `Page(data = emptyList(), prevKey = params.key, nextKey = null)` (end of list) |
| `Error` | calls `onError(result)`, then `LoadResult.Error(Exception())` |
| `Exception` | calls `onException(result)`, then `LoadResult.Error(Exception())` |
| thrown `Exception` | `LoadResult.Error(exception)` |

- The first load has `params.key == null`. Treat `null` as "first page" in `block`. `block` receives the key typed as `Any?`, so cast it to `KEY`.
- `calculateNextPage` defaults to identity, so the server's `pageId` is used as the **next** key. Return `null` from it (or a `null` `pageId`) to stop.
- `getRefreshKey` returns `null`, so a refresh restarts from the first page.
- `prevKey` is set to the **current** key rather than the previous page's key. Prepending from the first page works (its key is `null`), but if pages
  are ever dropped (`PagingConfig.maxSize`) and prepend runs, Paging 3 receives the same key twice and throws unless `keyReuseSupported = true`.
  Keep the default config (no `maxSize`).
- `Error`/`Exception` results become `LoadResult.Error(Exception())` **without a message**. Surface the details through the `onError`/`onException` callbacks.

```kotlin
val transactions: Flow<PagingData<TransactionDto>> = withUseCaseScopePaging<Int, TransactionDto>(
    pageSize = 20,
    onError = { setEffect { Effect.ShowError(it.response) } },
    block = { page -> getTransactions(page = (page as Int?) ?: 1) },   // returns ApiResult<PagingResponse<Int, TransactionDto>>
)
// UI: val items = viewModel.transactions.collectAsLazyPagingItems()
```
`PagingResponse` is in `ui/components/list/model` (see [components-layout](components-layout.md)). Paging's `PagingData`/`Pager` come from
`androidx.paging` (see [overview](overview.md) for which module declares the dependency).

## `coroutines/Utils.kt`
```kotlin
fun Flow<List<NavBackStackEntry>>.zipWithPreviousCount(): Flow<Pair<Int, List<NavBackStackEntry>>>
```
It emits `(previousListSize, currentList)` for every upstream list. It's used only by the legacy `BottomSheetHost` to keep a sheet composed while its
dismiss animation runs (see [navigation-legacy](navigation-legacy.md)).

## Related pages

- [Base architecture](base-architecture.md)
- [core-data network (`ApiResult`)](../core-data/network.md)
- [Lists and paging](components-layout.md)
- [Recipes](recipes.md)
