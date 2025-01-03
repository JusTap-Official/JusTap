package com.binay.shaw.justap.domain.model

/**
 * Created by binay on 01,January,2023
 */
data class User(
    val userID: String,
    val name: String,
    val email: String,
    val bio: String? = "",
    val profilePictureURI: String? = "",
    val profileBannerURI: String? = ""
)