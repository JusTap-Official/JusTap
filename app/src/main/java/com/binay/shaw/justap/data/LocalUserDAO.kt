package com.binay.shaw.justap.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.binay.shaw.justap.model.LocalUser

/**
 * Created by binay on 02,January,2023
 */

@Dao
interface LocalUserDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: LocalUser)

    @Query("DELETE FROM localDB")
    suspend fun deleteUser()

    @Update
    suspend fun updateUser(user: LocalUser)

    @Query("SELECT * FROM localDB")
    fun fetchLocalUser(): LiveData<LocalUser>

    @Query("SELECT userName FROM localDB")
    fun getName(): LiveData<String>

    @Query("SELECT userEmail FROM localDB")
    fun getEmail(): LiveData<String>

    @Query("SELECT userBio FROM localDB")
    fun getBio(): LiveData<String>

    @Query("SELECT userProfilePicture FROM localDB")
    fun getPFP(): LiveData<String>

    @Query("SELECT userBannerPicture FROM localDB")
    fun getBanner(): LiveData<String>

    @Query("SELECT userID FROM localDB")
    fun getID(): LiveData<String>

}