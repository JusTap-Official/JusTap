package com.binay.shaw.justap.repository

import androidx.lifecycle.LiveData
import com.binay.shaw.justap.data.LocalUserDAO
import com.binay.shaw.justap.model.LocalUser

/**
 * Created by binay on 03,January,2023
 */
class LocalUserRepository(
    private val localUserDAO: LocalUserDAO
) {

    val fetchUser: LiveData<LocalUser> = localUserDAO.fetchLocalUser()


    suspend fun insertUser(localUser: LocalUser) {
        localUserDAO.insertUser(localUser)
    }

    suspend fun deleteUser() {
        localUserDAO.deleteUser()
    }

    suspend fun updateUser(localUser: LocalUser) {
        localUserDAO.updateUser(localUser)
    }

    fun getName() : LiveData<String> = localUserDAO.getName()

    fun getBio() : LiveData<String> = localUserDAO.getBio()

    fun getID() : LiveData<String> = localUserDAO.getID()

    fun getEmail() : LiveData<String> = localUserDAO.getEmail()

}