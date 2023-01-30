package com.binay.shaw.justap.repository

import androidx.lifecycle.LiveData
import com.binay.shaw.justap.data.AccountsDAO
import com.binay.shaw.justap.model.Accounts

/**
 * Created by binay on 30,January,2023
 */
class AccountsRepository(
    private val accountsDAO: AccountsDAO
) {

    val getAccountsList: LiveData<List<Accounts>> = accountsDAO.getAccountsList()

    suspend fun insertAccount(accounts: Accounts) {
        accountsDAO.insertAccount(accounts)
    }

    suspend fun deleteAccount(accounts: Accounts) {
        accountsDAO.deleteAccount(accounts)
    }

    suspend fun updateAccount(newData: String, id: Int) {
        accountsDAO.updateAccount(data = newData, id = id)
    }
}