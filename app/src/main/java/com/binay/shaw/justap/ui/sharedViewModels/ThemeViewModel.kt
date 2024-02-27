package com.binay.shaw.justap.ui.sharedViewModels

import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.data.datastore.DataStoreUtil
import com.binay.shaw.justap.data.datastore.DataStoreUtil.Companion.IS_DARK_MODE_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ThemeState(val isDarkMode: Boolean)

@HiltViewModel
class ThemeViewModel @Inject constructor(dataStoreUtil: DataStoreUtil) : ViewModel() {

    private val _themeState = MutableStateFlow(ThemeState(false))
    val themeState: StateFlow<ThemeState> = _themeState

    private val dataStore = dataStoreUtil.dataStore

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.data.map { preferences ->
                ThemeState(preferences[IS_DARK_MODE_KEY] ?: false)
            }.collect {
                _themeState.value = it
            }
        }

    }

    fun toggleTheme() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[IS_DARK_MODE_KEY] = !(preferences[IS_DARK_MODE_KEY] ?: false)
            }
        }
    }
}