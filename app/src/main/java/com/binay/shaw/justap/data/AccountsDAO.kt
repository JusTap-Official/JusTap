package com.binay.shaw.justap.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.binay.shaw.justap.model.Accounts
import kotlinx.coroutines.flow.Flow


@Dao
interface AccountsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAccount(accounts: Accounts)

    @Delete
    suspend fun deleteAccount(accounts: Accounts)

    @Update
    suspend fun updateAccount(accounts: Accounts)

    @Query(value = "SELECT * FROM accountsDB")
    fun getAccountsList() : Flow<List<Accounts>>
}