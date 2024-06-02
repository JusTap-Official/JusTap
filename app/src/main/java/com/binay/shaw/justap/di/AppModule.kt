package com.binay.shaw.justap.di

import android.content.Context
import androidx.room.Room
import com.binay.shaw.justap.data.AccountsDAO
import com.binay.shaw.justap.data.LocalUserDAO
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.data.datastore.DataStoreUtil
import com.binay.shaw.justap.repository.AccountsRepository
import com.binay.shaw.justap.repository.LocalUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideDataStoreUtil(@ApplicationContext context: Context): DataStoreUtil =
        DataStoreUtil(context)

    @Provides
    @Singleton
    fun myDataBase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            LocalUserDatabase::class.java,
            "account_database"
        ).build()


    @Singleton
    @Provides
    fun provideAccountDao(db: LocalUserDatabase) = db.accountsDao()

    @Singleton
    @Provides
    fun provideDataStore(dataStoreUtil: DataStoreUtil) = dataStoreUtil.dataStore

    @Singleton
    @Provides
    fun provideLocalUserDao(db: LocalUserDatabase) = db.localUserDao()


    @Singleton
    @Provides
    fun provideLocalUserHistoryDao(db: LocalUserDatabase) = db.localUserHistoryDao()

    @Provides
    fun provideAccountRepository(accountsDAO: AccountsDAO): AccountsRepository =
        AccountsRepository(accountsDAO)

    @Provides
    fun provideLocalUserRepository(localUserDao: LocalUserDAO): LocalUserRepository =
        LocalUserRepository(localUserDao)

}