package com.binay.shaw.justap.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by binay on 02,January,2023
 */

@Entity(tableName = "localDB")
data class LocalUser(
    @PrimaryKey(autoGenerate = false)
    val userID: String,
    val userName: String,
    val userEmail: String,
    val userBio: String?,
    val userPhone: String?,
    val userProfileBase64: String?
)
