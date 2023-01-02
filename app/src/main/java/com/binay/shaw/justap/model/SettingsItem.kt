package com.binay.shaw.justap.model

import android.app.Activity

/**
 * Created by binay on 02,January,2023
 */

data class SettingsItem(
    val itemID: Int,
    val drawableInt: Int,
    val itemName: String,
    val isSwitchOn: Boolean,
    val activity: Activity? = null
)
