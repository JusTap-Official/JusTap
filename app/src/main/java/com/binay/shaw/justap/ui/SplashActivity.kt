package com.binay.shaw.justap.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.ui.authentication.SignInScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : Activity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_splash)

        //initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        if (Util.isUserLoggedIn(auth))
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