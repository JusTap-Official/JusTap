package com.binay.shaw.justap.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.room.*
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.LocalHistory
import java.io.ByteArrayOutputStream

/**
 * Created by binay on 10,February,2023
 */

@Dao
interface LocalHistoryDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserHistory(localHistory: LocalHistory)

    @Delete
    suspend fun deleteUserHistory(localHistory: LocalHistory)

    @Query(value = "DELETE FROM historyDB")
    fun clearHistory()

    @Query(value = "SELECT * FROM historyDB")
    fun getAllHistory() : LiveData<List<LocalHistory>>
}