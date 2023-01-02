package com.binay.shaw.justap.data

import androidx.room.*
import com.binay.shaw.justap.model.LocalUser

/**
 * Created by binay on 02,January,2023
 */

@Dao
interface LocalUserDAO {

    @Update
    suspend fun updateUser(user: LocalUser)

    @Query("SELECT userName FROM localDB")
    fun getName(): List<String>

    @Query("SELECT userEmail FROM localDB")
    fun getEmail(): List<String>

    @Query("SELECT userPhone FROM localDB")
    fun getPhone(): List<String>

    @Query("SELECT userBio FROM localDB")
    fun getBio(): List<String>

    @Query("SELECT userProfileBase64 FROM localDB")
    fun getPFP(): List<String>

    @Query("SELECT userID FROM localDB")
    fun getID(): List<String>

    @Query("SELECT * FROM localDB")
    fun fetchLocalUser(): List<LocalUser>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: LocalUser)

    @Query("DELETE FROM localDB")
    suspend fun deleteUser()

}