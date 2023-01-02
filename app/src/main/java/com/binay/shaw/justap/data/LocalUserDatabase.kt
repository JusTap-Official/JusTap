package com.binay.shaw.justap.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.binay.shaw.justap.model.LocalUser

/**
 * Created by binay on 02,January,2023
 */

@Database(entities = [LocalUser::class], version = 1)
abstract class LocalUserDatabase : RoomDatabase() {

    abstract fun localUserDao() : LocalUserDAO

}