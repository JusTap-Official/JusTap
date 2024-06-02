package com.binay.shaw.justap.presentation.mainScreens.historyScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.model.LocalHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class LocalHistoryViewModel(
    application: Application
) : AndroidViewModel(application) {

    val accountListLiveData = MutableLiveData<List<LocalHistory>>()

    private val _getAllHistory = MutableStateFlow<List<LocalHistory>>(emptyList())
    val getAllHistory get() = _getAllHistory.asStateFlow()

//    private val repository: LocalHistoryRepository

    init {
//        val dao = LocalUserDatabase.getDatabase(application).localUserHistoryDao()
//        repository = LocalHistoryRepository(dao)
        getAllHistory()
    }

    private fun getAllHistory() = viewModelScope.launch(Dispatchers.IO) {
//        repository.getAllHistory.collectLatest {
//            _getAllHistory.value = it
//        }
    }

    fun insertUserHistory(localHistory: LocalHistory) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.insertUserHistory(localHistory)
//        }
    }

    fun deleteUserHistory(localHistory: LocalHistory) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.deleteUserHistory(localHistory)
//        }
    }

    fun clearHistory() {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.clearHistory()
//        }
    }

}