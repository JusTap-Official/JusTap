package com.binay.shaw.justap.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.binay.shaw.justap.model.Accounts

/**
 * Created by binay on 30,January,2023
 */

@Dao
interface AccountsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAccount(accounts: Accounts)

    @Delete
    suspend fun deleteAccount(accounts: Accounts)

    @Query("DELETE FROM accountsDB WHERE accountID = :accountID")
    suspend fun deleteEntryById(accountID: Int)

    @Query("UPDATE accountsDB SET accountData = :data WHERE accountID = :id")
    fun updateAccount(data: String, id: Int)

    @Query(value = "SELECT * FROM accountsDB")
    fun getAccountsList() : LiveData<List<Accounts>>
}