package com.binay.shaw.justap.repository

import com.binay.shaw.justap.data.AccountsDAO
import com.binay.shaw.justap.model.Accounts
import kotlinx.coroutines.flow.Flow


class AccountsRepository(
    private val accountsDAO: AccountsDAO
) {

    val getAccountsList: Flow<List<Accounts>> = accountsDAO.getAccountsList()

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