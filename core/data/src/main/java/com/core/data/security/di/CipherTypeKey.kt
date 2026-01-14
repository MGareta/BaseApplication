package com.core.data.security.di

import com.core.data.security.model.CipherType
import dagger.MapKey

@MapKey
annotation class CipherTypeKey(val value: CipherType)
