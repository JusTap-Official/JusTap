package com.binay.shaw.justap.di.modules

import android.content.Context
import androidx.room.Room
import com.binay.shaw.justap.data.local.database.LocalUserDatabase
import com.binay.shaw.justap.data.local.db.AccountsDAO
import com.binay.shaw.justap.data.local.db.LocalHistoryDAO
import com.binay.shaw.justap.data.local.db.LocalUserDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // Provides the LocalUserDatabase instance
    @Provides
    @Singleton
    fun provideLocalUserDatabase(context: Context): LocalUserDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            LocalUserDatabase::class.java,
            "account_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    // Provides LocalUserDAO from the database
    @Provides
    fun provideLocalUserDao(database: LocalUserDatabase): LocalUserDAO {
        return database.localUserDao()
    }

    // Provides AccountsDAO from the database
    @Provides
    fun provideAccountsDao(database: LocalUserDatabase): AccountsDAO {
        return database.accountsDao()
    }

    // Provides LocalHistoryDAO from the database
    @Provides
    fun provideLocalHistoryDao(database: LocalUserDatabase): LocalHistoryDAO {
        return database.localUserHistoryDao()
    }
}