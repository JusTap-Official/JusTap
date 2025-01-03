package com.binay.shaw.justap.domain.repository

import androidx.lifecycle.LiveData
import com.binay.shaw.justap.data.local.db.AccountsDAO
import com.binay.shaw.justap.domain.model.Accounts


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

    suspend fun updateAccount(accounts: Accounts) {
        accountsDAO.updateAccount(accounts)
    }

}