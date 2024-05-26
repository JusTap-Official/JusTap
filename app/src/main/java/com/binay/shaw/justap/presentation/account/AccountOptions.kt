package com.binay.shaw.justap.presentation.account

import com.binay.shaw.justap.R

enum class AccountOptions(val id: Int, val displayName: String, val iconId: Int) {
    EDIT_PROFILE(0, "Edit Profile", R.drawable.ic_account),
    CUSTOMIZE_QR(1, "Customize QR", R.drawable.ic_customize_qr),
    THEME(2, "Theme", R.drawable.ic_theme),
    INVITE_FRIENDS(3, "Invite your friends", R.drawable.ic_invite_friends),
    LANGUAGE(4, "Language", R.drawable.ic_language),
    PRIVACY_POLICY(5, "Privacy Policy", R.drawable.ic_privacy_policy),
    RATE_US(6, "Rate us", R.drawable.ic_google_playstore),
    HELP_AND_SUPPORT(7, "Help and Support", R.drawable.ic_help),
    LOGOUT(8, "Logout", R.drawable.ic_logout);
}