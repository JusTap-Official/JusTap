package com.binay.shaw.justap.domain.repository

import com.binay.shaw.justap.domain.model.Accounts
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    suspend fun getAccounts(): Flow<List<Accounts>>

    suspend fun insertAccount(accounts: Accounts): Long

    suspend fun deleteAccount(accounts: Accounts): Int

    suspend fun updateAccount(accounts: Accounts): Int
}