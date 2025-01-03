package com.binay.shaw.justap.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

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
    val userProfilePicture: String?,
    val userBannerPicture: String?
) : Serializable
