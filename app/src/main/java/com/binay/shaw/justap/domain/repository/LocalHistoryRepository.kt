package com.binay.shaw.justap.domain.repository

import androidx.lifecycle.LiveData
import com.binay.shaw.justap.data.local.db.LocalHistoryDAO
import com.binay.shaw.justap.domain.model.LocalHistory

/**
 * Created by binay on 10,February,2023
 */
class LocalHistoryRepository(
    private val historyDAO: LocalHistoryDAO
) {

    val getAllHistory: LiveData<List<LocalHistory>> = historyDAO.getAllHistory()

    suspend fun insertUserHistory(localHistory: LocalHistory) {
        historyDAO.insertUserHistory(localHistory)
    }

    suspend fun deleteUserHistory(localHistory: LocalHistory) {
        historyDAO.deleteUserHistory(localHistory)
    }

    fun clearHistory() {
        historyDAO.clearHistory()
    }

}