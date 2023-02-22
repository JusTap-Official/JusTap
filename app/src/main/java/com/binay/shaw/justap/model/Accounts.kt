package com.binay.shaw.justap.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "accountsDB")
data class Accounts(
    @PrimaryKey(autoGenerate = false)
    var accountID: Int,
    val accountName: String,
    var accountData: String,
    var showAccount: Boolean
) : Serializable
