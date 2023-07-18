package com.binay.shaw.justap.helper

import android.content.Context
import android.content.res.Configuration
import androidx.fragment.app.Fragment
import java.util.Locale


fun Fragment.setLocate(lang: String) {
    val locale = Locale(lang)
    Locale.setDefault(locale)

    val config = Configuration()
    config.locale = locale
    requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)

    val editor = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
    editor.putString("My_Lang", lang)
    editor.apply()
}