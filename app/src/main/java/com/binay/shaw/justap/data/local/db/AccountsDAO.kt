package com.binay.shaw.justap.data.local.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.binay.shaw.justap.domain.model.Accounts


@Dao
interface AccountsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAccount(accounts: Accounts)

    @Delete
    suspend fun deleteAccount(accounts: Accounts)

    @Update
    suspend fun updateAccount(accounts: Accounts)

    @Query(value = "SELECT * FROM accountsDB")
    fun getAccountsList() : LiveData<List<Accounts>>
}