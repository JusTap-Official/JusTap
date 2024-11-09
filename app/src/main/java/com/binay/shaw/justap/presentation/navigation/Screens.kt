package com.binay.shaw.justap.presentation.navigation

import com.binay.shaw.justap.utilities.Constants

sealed class Screens(val name: String) {
    data object HomeScreen : Screens(Constants.HOME_SCREEN)
    data object ConnectScreen : Screens(Constants.CONNECT_SCREEN)
    data object HistoryScreen : Screens(Constants.HISTORY_SCREEN)
    data object AccountScreen : Screens(Constants.ACCOUNT_SCREEN)
    data object ConnectScannerScreen : Screens(Constants.CONNECT_SCANNER_SCREEN)
    data object ContactDetailsScreen : Screens(Constants.CONTACT_DETAILS_SCREEN)
}


