package com.binay.shaw.justap


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.binay.shaw.justap.databinding.ActivityMainBinding
import com.binay.shaw.justap.ui.authentication.SignIn_Screen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this@MainActivity, SignIn_Screen::class.java)).also { finish() }
        }

        if (Util.isDarkMode(baseContext)) {
            binding.UIModeSwitch.isChecked = true
        }

        binding.UIModeSwitch.setOnClickListener {
            switchTheme()
        }

    }

    private fun switchTheme() {
        sharedPreferences = getSharedPreferences("ThemeHandler", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("FIRST_TIME", false)
        if (Util.isDarkMode(baseContext)) {
            editor.putBoolean("DARK_MODE", false)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            editor.putBoolean("DARK_MODE", true)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        editor.apply()
    }
}