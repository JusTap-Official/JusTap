package com.binay.shaw.justap.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.domain.model.Accounts
import com.binay.shaw.justap.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {


    var accounts = MutableStateFlow<List<Accounts>>(emptyList())
        private set


    init {
        runIO {
            repository.getAccounts().collect {
                accounts.value = it
            }
        }
    }

    fun deleteAccount(accounts: Accounts) = runIO {
        repository.deleteAccount(accounts)
    }

    fun updateAccount(accounts: Accounts) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAccount(accounts)
        }

    fun insertAccount(accounts: Accounts) = runIO {
        repository.insertAccount(accounts)
    }

    private fun runIO(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block()
        }
    }
}