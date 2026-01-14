package com.core.presentation.ui.components.list.model

data class PagingResponse<KEY, ITEM>(val pageId: KEY?, val data: List<ITEM>)
