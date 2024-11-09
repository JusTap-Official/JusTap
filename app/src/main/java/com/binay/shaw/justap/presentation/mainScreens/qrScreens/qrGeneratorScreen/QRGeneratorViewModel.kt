package com.binay.shaw.justap.presentation.mainScreens.qrScreens.qrGeneratorScreen

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class QRGeneratorViewModel @Inject constructor() : ViewModel() {
    private val _userId = MutableStateFlow<String?>(null)
    val userId = _userId.asStateFlow()

    fun fetchFirebaseUserId() {
        _userId.update {
            Firebase.auth.currentUser?.uid
        }
    }
}