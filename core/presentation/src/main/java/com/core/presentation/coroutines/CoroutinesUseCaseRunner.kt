package com.core.presentation.coroutines

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.core.data.network.model.ApiResult
import com.core.presentation.base.model.UiResult
import com.core.presentation.ui.components.list.model.PagingResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

interface CoroutinesUseCaseRunner {
    val useCaseCoroutineScope: CoroutineScope

    fun withUseCaseScope(
        loadingUpdater: ((Boolean) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
        vararg block: (suspend () -> Unit),
    ) {
        useCaseCoroutineScope.launch {
            loadingUpdater?.invoke(true)
            try {
                block
                    .map {
                        launch { it() }
                    }.joinAll()
            } catch (e: Exception) {
                onError?.invoke(e)
            } finally {
                loadingUpdater?.invoke(false)
                onComplete?.invoke()
            }
        }
    }

    fun <T> withUseCaseScopeLazily(
        initialValue: UiResult<T> = UiResult.Loading(),
        onError: ((ApiResult.Error<T>) -> UiResult.Error<T>),
        onException: ((ApiResult.Exception<T>) -> UiResult.Error<T>),
        block: (suspend () -> ApiResult<T>),
    ): StateFlow<UiResult<T>> = flow {
        emit(
            when (val response = block()) {
                is ApiResult.Success -> UiResult.Success(data = response.data)
                is ApiResult.Error -> onError.invoke(response)
                is ApiResult.Exception -> onException.invoke(response)
                is ApiResult.NoContent -> UiResult.NoContent()
            },
        )
    }.stateIn(
        scope = useCaseCoroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialValue,
    )

    fun <KEY : Any, ITEM : Any> withUseCaseScopePaging(
        pageSize: Int,
        onError: ((ApiResult.Error<PagingResponse<KEY, ITEM>>) -> Unit)? = null,
        onException: ((ApiResult.Exception<PagingResponse<KEY, ITEM>>) -> Unit)? = null,
        calculateNextPage: (page: KEY?) -> KEY? = { it },
        keyReuseSupported: Boolean = false,
        block: (suspend (page: Any?) -> ApiResult<PagingResponse<KEY, ITEM>>),
    ): Flow<PagingData<ITEM>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
            ),
            pagingSourceFactory = {
                object : PagingSource<KEY, ITEM>() {
                    override val keyReuseSupported: Boolean
                        get() = keyReuseSupported

                    override fun getRefreshKey(state: PagingState<KEY, ITEM>): KEY? = null

                    override suspend fun load(params: LoadParams<KEY>): LoadResult<KEY, ITEM> {
                        return try {
                            val page = params.key

                            return when (val response = block(page)) {
                                is ApiResult.Success -> {
                                    LoadResult.Page(
                                        data = response.data.data,
                                        prevKey = page,
                                        nextKey = calculateNextPage(response.data.pageId),
                                    )
                                }

                                is ApiResult.NoContent -> LoadResult.Page(
                                    data = emptyList(),
                                    prevKey = page,
                                    nextKey = null,
                                )

                                is ApiResult.Error -> {
                                    onError?.invoke(response)
                                    LoadResult.Error(throwable = Exception())
                                }

                                is ApiResult.Exception -> {
                                    onException?.invoke(response)
                                    LoadResult.Error(throwable = Exception())
                                }
                            }
                        } catch (exception: Exception) {
                            LoadResult.Error(throwable = exception)
                        }
                    }
                }
            },
        ).flow.cachedIn(useCaseCoroutineScope)
    }
}
