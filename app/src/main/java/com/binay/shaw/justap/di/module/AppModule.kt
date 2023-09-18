package com.binay.shaw.justap.di.module

import com.binay.shaw.justap.data.AccountsDAO
import com.binay.shaw.justap.data.LocalUserDatabase
import dagger.hilt.components.SingletonComponent
import android.app.Application
import android.content.Context
import com.binay.shaw.justap.JusTapApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(@ApplicationContext application: JusTapApplication): Context =
        application.applicationContext

    @Singleton
    @Provides
    fun provideDatabase(app: Application): LocalUserDatabase {
        return LocalUserDatabase.getDatabase(app)
    }

    @Singleton
    @Provides
    fun provideUserDao(database: LocalUserDatabase): AccountsDAO {
        return database.accountsDao()
    }
}