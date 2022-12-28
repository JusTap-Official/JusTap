package com.binay.shaw.justap.ui.authentication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.ActivitySignUpScreenBinding

class SignUp_Screen : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivitySignUpScreenBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)
        supportActionBar?.hide()
        findViewById<TextView>(R.id.toolbar_title).text = "Create Account"

        passwordVisibilityHandler()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun passwordVisibilityHandler() {
        // Hide and Show Password
        var passwordVisible = false
        binding.etPassword.setOnTouchListener { _, event ->
            val right = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.etPassword.right - binding.etPassword.compoundDrawables[right].bounds.width()
                ) {
                    val selection: Int = binding.etPassword.selectionEnd
                    //Handles Multiple option popups
                    if (passwordVisible) {
                        //set drawable image here
                        binding.etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.visibility_on, 0)
                        //for hide password
                        binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                        passwordVisible = false
                    } else {
                        //set drawable image here
                        binding.etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.visibility_off, 0)
                        //for show password
                        binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        passwordVisible = true
                    }
                    binding.etPassword.isLongClickable = false //Handles Multiple option popups
                    binding.etPassword.setSelection(selection)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}