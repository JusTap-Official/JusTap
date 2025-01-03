package com.binay.shaw.justap.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.data.local.database.LocalUserDatabase
import com.binay.shaw.justap.domain.model.Accounts
import com.binay.shaw.justap.domain.repository.AccountsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AccountsViewModel(
    application: Application
) : AndroidViewModel(application) {


    val getAllUser : LiveData<List<Accounts>>
    private val repository: AccountsRepository


    init {
        val dao = LocalUserDatabase.getDatabase(application).accountsDao()
        repository = AccountsRepository(dao)
        getAllUser = repository.getAccountsList
    }

    fun deleteAccount(accounts: Accounts) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAccount(accounts)
        }

    fun updateAccount(accounts: Accounts) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAccount(accounts)
        }

    fun insertAccount(accounts: Accounts) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAccount(accounts)
        }

}