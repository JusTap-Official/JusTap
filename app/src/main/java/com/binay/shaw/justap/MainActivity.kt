package com.binay.shaw.justap


import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.binay.shaw.justap.databinding.ActivityMainBinding
import com.binay.shaw.justap.ui.authentication.SignIn_Screen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this@MainActivity, SignIn_Screen::class.java))
        }


        if (Util.isDarkMode(baseContext)) {
            Toast.makeText(this@MainActivity, "Is Dark Mode", Toast.LENGTH_LONG).show()
            binding.UIModeSwitch.isChecked = true
        }
        else
            Toast.makeText(this@MainActivity, "Is Not Dark Mode", Toast.LENGTH_LONG).show()


        binding.UIModeSwitch.setOnClickListener {
            if (Util.isDarkMode(baseContext))
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

    }
}