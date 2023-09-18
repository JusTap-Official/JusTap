package com.binay.shaw.justap.di.module

import com.binay.shaw.justap.data.AccountsDAO
import com.binay.shaw.justap.repository.AccountsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object RepositoryModule {
    @Provides
    fun provideUserRepository(accountsDao: AccountsDAO): AccountsRepository {
        return AccountsRepository(accountsDao)
    }
}