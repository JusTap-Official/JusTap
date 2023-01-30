package com.binay.shaw.justap.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Created by binay on 27,January,2023
 */

@Entity(tableName = "accountsDB")
data class Accounts(
    @PrimaryKey(autoGenerate = false)
    val accountID: Int,
    val accountName: String,
    val accountData: String,
    val showAccount: Boolean
) : Serializable
