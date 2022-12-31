package com.binay.shaw.justap.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.ui.authentication.SignIn_Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : Activity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private var isDarkMode: Boolean = true
    private var isFirstTime: Boolean = true
    private var isCurrentThemeIsDarkMode: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_splash)

//        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance(); //initialize Firebase Auth

        if (checkLoggedInState()) {
            splashToBase()
        } else
            splashToLogIn()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun splashToLogIn() {
        GlobalScope.launch {
            delay(2000)

            val intent = Intent(this@SplashActivity, SignIn_Screen::class.java)
            startActivity(intent)
            finish()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun splashToBase() {
        GlobalScope.launch {
            delay(2000)

            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setTheme() {
        isCurrentThemeIsDarkMode = Util.isDarkMode(baseContext) //Gives the current theme
        sharedPreferences =  getSharedPreferences("ThemeHandler", Context.MODE_PRIVATE)
        isDarkMode = sharedPreferences.getBoolean("DARK_MODE", true)    //Last edited theme
        isFirstTime = sharedPreferences.getBoolean("FIRST_TIME", true)  //First time changes the theme

        //Opened more than one time
        if (!isFirstTime) {
            //Changes needed are to be dark mode
            if (isDarkMode) {
                //if Current Theme is not dark mode
                if (!isCurrentThemeIsDarkMode) {
                    //Set to dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            } else {
                //Changes require are to be light mode
                if (isCurrentThemeIsDarkMode) {
                    //Set to light mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }

    private fun checkLoggedInState() : Boolean {
        // not logged in
        return if (auth.currentUser == null) {
            Util.log("You are not logged in")
            false
        } else {
            Util.log("You are logged in!")
            true
        }
    }

}