package com.core.data.security.di

import com.core.data.security.cryptography.ICryptographyType
import com.core.data.security.cryptography.aes.CryptographyAesType
import com.core.data.security.cryptography.rsa.CryptographyRsaType
import com.core.data.security.manager.ISecurityManager
import com.core.data.security.manager.SecurityManagerImpl
import com.core.data.security.model.CipherType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    @Provides
    @IntoMap
    @CipherTypeKey(CipherType.RSA)
    fun providesCryptographyRsaType(): ICryptographyType = CryptographyRsaType()

    @Provides
    @IntoMap
    @CipherTypeKey(CipherType.AES)
    fun providesCryptographyAesType(): ICryptographyType = CryptographyAesType()

    @Singleton
    @Provides
    fun providesSecurityManager(
        cryptography: Map<@JvmSuppressWildcards CipherType, @JvmSuppressWildcards ICryptographyType>,
    ): ISecurityManager = SecurityManagerImpl(
        cryptography = cryptography,
    )
}
