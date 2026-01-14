package com.core.presentation.ui.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.core.presentation.ui.components.list.utils.Error
import com.core.presentation.ui.components.list.utils.Loading
import kotlinx.coroutines.flow.Flow

@Composable
fun <ITEM : Any> PagingComponent(
    modifier: Modifier,
    verticalArrangement: Arrangement.Vertical,
    contentPadding: PaddingValues,
    items: Flow<PagingData<ITEM>>,
    content: @Composable (ITEM) -> Unit,
) {
    val pagingItems = items.collectAsLazyPagingItems()

    LazyColumn(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        contentPadding = contentPadding,
    ) {
        when (val state = pagingItems.loadState.prepend) {
            is LoadState.NotLoading -> Unit
            is LoadState.Loading -> {
                Loading()
            }

            is LoadState.Error -> {
                Error(message = state.error.message ?: "")
            }
        }

        when (val state = pagingItems.loadState.refresh) {
            is LoadState.NotLoading -> Unit
            is LoadState.Loading -> {
                Loading()
            }

            is LoadState.Error -> {
                Error(message = state.error.message ?: "")
            }
        }

        items(
            count = pagingItems.itemCount,
            key = {
                pagingItems[it].hashCode()
            },
            contentType = {
            },
        ) {
            pagingItems[it]?.let {
                content(it)
            }
        }

        when (val state = pagingItems.loadState.append) {
            is LoadState.NotLoading -> Unit
            is LoadState.Loading -> {
                Loading()
            }

            is LoadState.Error -> {
                Error(message = state.error.message ?: "")
            }
        }
    }
}
