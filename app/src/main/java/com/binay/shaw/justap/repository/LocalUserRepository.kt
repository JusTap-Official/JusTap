package com.binay.shaw.justap.repository

import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.model.LocalUser

/**
 * Created by binay on 03,January,2023
 */
class LocalUserRepository(
    private val localUserDatabase: LocalUserDatabase
) {

    suspend fun insertUser(user: LocalUser) = localUserDatabase.localUserDao().insertUser(user)


    suspend fun updateUser(user: LocalUser) = localUserDatabase.localUserDao().updateUser(user)

    suspend fun deleteUser() = localUserDatabase.localUserDao().deleteUser()

    fun getName(): List<String> = localUserDatabase.localUserDao().getName()

    fun getEmail(): List<String> = localUserDatabase.localUserDao().getEmail()

    fun getPhone(): List<String> = localUserDatabase.localUserDao().getPhone()

    fun getBio(): List<String> = localUserDatabase.localUserDao().getBio()

    fun getPFP(): List<String> = localUserDatabase.localUserDao().getPFP()

    fun getID(): List<String> = localUserDatabase.localUserDao().getID()

    fun fetchUser(): List<LocalUser> = localUserDatabase.localUserDao().fetchLocalUser()
}