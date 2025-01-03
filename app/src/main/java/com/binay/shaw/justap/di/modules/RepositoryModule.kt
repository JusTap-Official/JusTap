package com.binay.shaw.justap.di.modules

import com.binay.shaw.justap.data.local.db.AccountsDAO
import com.binay.shaw.justap.data.local.repository.AccountRepositoryImpl
import com.binay.shaw.justap.domain.repository.AccountRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideAccountRepository(accountsDAO: AccountsDAO): AccountRepository {
        return AccountRepositoryImpl(accountsDAO)
    }
}
