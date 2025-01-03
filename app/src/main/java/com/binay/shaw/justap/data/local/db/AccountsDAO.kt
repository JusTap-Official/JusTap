package com.binay.shaw.justap.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.binay.shaw.justap.domain.model.Accounts
import kotlinx.coroutines.flow.Flow


@Dao
interface AccountsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAccount(accounts: Accounts): Long

    @Delete
    suspend fun deleteAccount(accounts: Accounts): Int

    @Update
    suspend fun updateAccount(accounts: Accounts): Int

    @Query(value = "SELECT * FROM accountsDB")
    fun getAccountsList(): Flow<List<Accounts>>
}