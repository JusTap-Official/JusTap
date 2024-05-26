package com.binay.shaw.justap.presentation.account

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.ui.graphics.vector.ImageVector

enum class AccountOptions(val id: Int, val displayName: String, val icon: ImageVector) {
    EDIT_PROFILE(0, "Edit Profile", Icons.Outlined.Person),
    CUSTOMIZE_QR(1, "Customize QR", Icons.Default.QrCode),
    THEME(2, "Theme", Icons.Default.LightMode),
    INVITE_FRIENDS(3, "Invite your friends", Icons.Outlined.GroupAdd),
    LANGUAGE(4, "Language", Icons.Outlined.Language),
    PRIVACY_POLICY(5, "Privacy Policy", Icons.Outlined.PrivacyTip),
    RATE_US(6, "Rate us", Icons.Outlined.RateReview),
    HELP_AND_SUPPORT(7, "Help and Support", Icons.AutoMirrored.Outlined.Help),
    LOGOUT(8, "Logout", Icons.AutoMirrored.Outlined.Logout);
}