package com.binay.shaw.justap.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.repository.AccountsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel
@Inject constructor(private val repository: AccountsRepository) :
    ViewModel()
{


    private val _userAccountList = MutableStateFlow<List<Accounts>>(emptyList())
    val userAccountList get() = _userAccountList.asStateFlow()


    fun getAllUserAccounts() = viewModelScope.launch(Dispatchers.IO) {
       repository.getAccountsList.collectLatest {
           _userAccountList.value = it
        }
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