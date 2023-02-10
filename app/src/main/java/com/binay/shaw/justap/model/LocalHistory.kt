package com.binay.shaw.justap.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Created by binay on 10,February,2023
 */

@Entity(tableName = "historyDB")
data class LocalHistory(
    @PrimaryKey(autoGenerate = false)
    var userID: String,
    val userPFPBase64: String?
)
