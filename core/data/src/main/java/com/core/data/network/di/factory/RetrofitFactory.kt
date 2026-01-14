package com.core.data.network.di.factory

import com.core.data.network.di.builder.RetrofitBuilder
import dagger.assisted.AssistedFactory

@AssistedFactory
fun interface RetrofitFactory {
    fun create(): RetrofitBuilder
}
