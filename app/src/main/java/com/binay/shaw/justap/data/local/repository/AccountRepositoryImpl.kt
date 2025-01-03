package com.binay.shaw.justap.data.local.repository

import com.binay.shaw.justap.data.local.db.AccountsDAO
import com.binay.shaw.justap.domain.model.Accounts
import com.binay.shaw.justap.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountsDao: AccountsDAO
) : AccountRepository {

    override suspend fun getAccounts(): Flow<List<Accounts>> {
        return accountsDao.getAccountsList()
    }

    override suspend fun insertAccount(accounts: Accounts): Long {
        return accountsDao.insertAccount(accounts)
    }

    override suspend fun deleteAccount(accounts: Accounts): Int {
        return accountsDao.deleteAccount(accounts)
    }

    override suspend fun updateAccount(accounts: Accounts): Int {
        return accountsDao.updateAccount(accounts)
    }
}