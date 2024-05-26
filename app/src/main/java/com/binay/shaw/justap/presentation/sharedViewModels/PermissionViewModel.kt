package com.binay.shaw.justap.presentation.sharedViewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor() : ViewModel() {

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        Timber.d("On Dialog Dismiss")
        if (visiblePermissionDialogQueue.isNotEmpty())
            visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        Timber.d("Permission is: $permission and isGranted: $isGranted")
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    fun clearPermissionRequests() {
        Timber.d("Cleared permission request queue")
        visiblePermissionDialogQueue.clear()
    }
}