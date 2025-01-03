package com.binay.shaw.justap.presentation.intro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.binay.shaw.justap.presentation.main.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseActivity
import com.binay.shaw.justap.utils.Constants
import com.binay.shaw.justap.utils.Util
import com.binay.shaw.justap.presentation.auth.sign_in.SignInScreen
import com.binay.shaw.justap.presentation.intro.onboarding.OnboardingScreen
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        makeIntent(Util.isUserLoggedIn())
    }

    private fun makeIntent(userLoggedIn: Boolean) {
        val intent = if (userLoggedIn) {
            Intent(this@SplashActivity, MainActivity::class.java)
        } else if (onBoardingScreenViewed()) {
            Intent(this@SplashActivity, SignInScreen::class.java)
        } else {
            Intent(this@SplashActivity, OnboardingScreen::class.java)
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            startActivity(intent)
            finish()
        }
    }

    private fun onBoardingScreenViewed(): Boolean {
        val pref =
            applicationContext.getSharedPreferences(Constants.onBoardingPref, MODE_PRIVATE)
        return pref.getBoolean(Constants.isIntroOpened, false)
    }
}