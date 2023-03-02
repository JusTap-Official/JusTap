package com.binay.shaw.justap.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseActivity
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.ui.authentication.signInScreen.SignInScreen
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        makeIntent(Util.isUserLoggedIn())
    }

    private fun makeIntent(userLoggedIn: Boolean) {
        val intent = if (userLoggedIn) {
            Intent(this@SplashActivity, MainActivity::class.java)
        } else {
            Intent(this@SplashActivity, SignInScreen::class.java)
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            startActivity(intent)
            finish()
        }
    }
}