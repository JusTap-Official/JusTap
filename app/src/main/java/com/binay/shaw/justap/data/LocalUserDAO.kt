package com.binay.shaw.justap.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.binay.shaw.justap.model.LocalUser

/**
 * Created by binay on 02,January,2023
 */

@Dao
interface LocalUserDAO {

    @Update
    suspend fun updateUser(user: LocalUser)

    @Query("SELECT * FROM localDB")
    fun fetchLocalUser(): LiveData<List<LocalUser>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: LocalUser)

    @Query("DELETE FROM localDB")
    suspend fun deleteUser()

}