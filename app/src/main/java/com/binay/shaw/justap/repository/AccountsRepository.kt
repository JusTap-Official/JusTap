package com.binay.shaw.justap.repository

import androidx.lifecycle.LiveData
import com.binay.shaw.justap.data.AccountsDAO
import com.binay.shaw.justap.model.Accounts
import javax.inject.Inject


class AccountsRepository
@Inject constructor(
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