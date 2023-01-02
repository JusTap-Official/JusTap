package com.binay.shaw.justap.viewModel

import androidx.lifecycle.ViewModel
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.repository.LocalUserRepository

/**
 * Created by binay on 03,January,2023
 */

class LocalUserViewModel(
    private val repository: LocalUserRepository
) : ViewModel() {

    suspend fun insertUser(user: LocalUser) = repository.insertUser(user)

    suspend fun updateUser(user: LocalUser) = repository.updateUser(user)

    suspend fun deleteUser() = repository.deleteUser()

    fun getID() = repository.getID()

    fun getName() = repository.getName()

    fun getEmail() = repository.getEmail()

    fun getPhone() = repository.getPhone()

    fun getBio() = repository.getBio()

    fun getPFP() = repository.getPFP()

    fun fetchUser() = repository.fetchUser()

}