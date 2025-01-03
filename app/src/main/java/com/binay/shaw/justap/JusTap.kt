package com.binay.shaw.justap

import android.app.Application
import com.binay.shaw.justap.utils.Logger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class JusTap: Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.debugLog("JusTap Application Created")
    }
}