package com.binay.shaw.justap.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.repository.AccountsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val repository: AccountsRepository,
) : ViewModel() {


    val getAllUser: LiveData<List<Accounts>> = repository.getAccountsList


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