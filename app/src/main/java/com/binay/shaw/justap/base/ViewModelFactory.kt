package com.binay.shaw.justap.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.binay.shaw.justap.ui.mainScreens.historyScreen.LocalHistoryViewModel
import com.binay.shaw.justap.ui.mainScreens.homeScreen.accountFragments.AddEditViewModel
import com.binay.shaw.justap.ui.mainScreens.qrScreens.qrGeneratorScreen.QRGeneratorViewModel
import com.binay.shaw.justap.ui.mainScreens.resultScreen.ScanResultViewModel
import com.binay.shaw.justap.ui.mainScreens.settingsScreen.editScreen.EditProfileViewModel
import com.binay.shaw.justap.viewModel.FirebaseViewModel

/**
 * Created by binay on 02,March,2023
 */
class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        with(modelClass) {
            // Get the Application object from extras
            val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
            when {
                isAssignableFrom(LocalHistoryViewModel::class.java) -> {
                    LocalHistoryViewModel(application)
                }
                isAssignableFrom(AddEditViewModel::class.java) -> {
                    AddEditViewModel()
                }
                isAssignableFrom(QRGeneratorViewModel::class.java) -> {
                    QRGeneratorViewModel()
                }
                isAssignableFrom(ScanResultViewModel::class.java) -> {
                    ScanResultViewModel()
                }
                isAssignableFrom(EditProfileViewModel::class.java) -> {
                    EditProfileViewModel()
                }
                isAssignableFrom(AccountsViewModel::class.java) -> {
                    AccountsViewModel(application)
                }
                isAssignableFrom(LocalUserViewModel::class.java) -> {
                    LocalUserViewModel(application)
                }
                isAssignableFrom(FirebaseViewModel::class.java) -> {
                    FirebaseViewModel()
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class")
            }
        } as T
}