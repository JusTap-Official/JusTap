package com.binay.shaw.justap.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.binay.shaw.justap.data.local.db.AccountsDAO
import com.binay.shaw.justap.data.local.db.LocalHistoryDAO
import com.binay.shaw.justap.data.local.db.LocalUserDAO
import com.binay.shaw.justap.domain.model.Accounts
import com.binay.shaw.justap.domain.model.Converters
import com.binay.shaw.justap.domain.model.LocalHistory
import com.binay.shaw.justap.domain.model.LocalUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [LocalUser::class, Accounts::class, LocalHistory::class], version = 4)
@TypeConverters(Converters::class)
abstract class LocalUserDatabase : RoomDatabase() {

    abstract fun localUserDao(): LocalUserDAO

    abstract fun accountsDao(): AccountsDAO

    abstract fun localUserHistoryDao(): LocalHistoryDAO

    companion object {
        @Volatile
        private var INSTANCE: LocalUserDatabase? = null

        fun getDatabase(context: Context): LocalUserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalUserDatabase::class.java,
                    "account_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

    fun clearTables() {
        CoroutineScope(Dispatchers.IO).launch {
            this@LocalUserDatabase.clearAllTables()
        }
    }
}