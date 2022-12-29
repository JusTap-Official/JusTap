package com.binay.shaw.justap

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.Log

/**
 * Created by binay on 29,December,2022
 */
class Util {
    companion object {
        fun log(message: String) {
            Log.d("", message)
        }

        fun isDarkMode(context: Context): Boolean {
            return context.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }
    }
}