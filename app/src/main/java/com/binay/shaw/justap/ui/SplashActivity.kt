package com.binay.shaw.justap.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.ui.authentication.signInScreen.SignInScreen
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_splash)

        if (Util.isUserLoggedIn())
            splashToBase()
        else
            splashToLogIn()
    }

    private fun splashToLogIn() {
        CoroutineScope(Dispatchers.Main).launch {
        delay(2000)
            val intent = Intent(this@SplashActivity, SignInScreen::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun splashToBase() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}