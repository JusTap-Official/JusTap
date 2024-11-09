package com.binay.shaw.justap.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.repository.LocalUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalUserViewModel @Inject constructor(
    private val repository: LocalUserRepository
) : ViewModel() {

    private val _user = MutableStateFlow(LocalUser(userName = "Loading..."))
    val user get() = _user.asStateFlow()

    fun getUser() = viewModelScope.launch(Dispatchers.IO) {
        repository.fetchUser.collectLatest {
            _user.value = it
        }
    }

    fun deleteUser() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteUser()
    }

    fun updateUser(localUser: LocalUser) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateUser(localUser)
    }

    fun insertUser(localUser: LocalUser) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertUser(localUser)
    }

}