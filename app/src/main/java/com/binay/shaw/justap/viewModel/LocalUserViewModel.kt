package com.binay.shaw.justap.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.repository.LocalUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by binay on 03,January,2023
 */

class LocalUserViewModel(
    application: Application
) : AndroidViewModel(application) {


    val fetchUser: LiveData<LocalUser>
    val name: LiveData<String>

    val repository: LocalUserRepository


    init {
        val dao = LocalUserDatabase.getDatabase(application).localUserDao()
        repository = LocalUserRepository(dao)
        fetchUser = repository.fetchUser
        name = repository.getName()
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