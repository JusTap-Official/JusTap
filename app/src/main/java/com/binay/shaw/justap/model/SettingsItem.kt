package com.binay.shaw.justap.model


data class SettingsItem(
    val itemID: Int,
    val drawableInt: Int,
    val itemName: String,
    val isSwitchOn: Boolean,
)

enum class SettingsState(int: Int) {
    TO_EDIT_PROFILE(0),
    TO_CUSTOMIZE_QR(1),
    TO_ABOUT_US(2),
    TO_NEED_HELP(3),
    TO_DARK_MODE(4),
    TO_RATE_APP(5),
    TO_LOGOUT(6);

    companion object {
        fun getSettingsState(state: SettingsState) : Int {
            return when (state) {
                TO_EDIT_PROFILE -> 0
                TO_CUSTOMIZE_QR -> 1
                TO_ABOUT_US -> 2
                TO_NEED_HELP -> 3
                TO_DARK_MODE-> 4
                TO_RATE_APP-> 5
                TO_LOGOUT-> 6
            }
        }
    }
}
