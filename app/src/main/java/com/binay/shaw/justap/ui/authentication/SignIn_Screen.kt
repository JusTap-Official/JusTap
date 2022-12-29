package com.binay.shaw.justap.ui.authentication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.Util
import com.binay.shaw.justap.databinding.ActivitySignInScreenBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignIn_Screen : AppCompatActivity() {

    private lateinit var binding: ActivitySignInScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivitySignInScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        findViewById<TextView>(R.id.toolbar_title).text = "Log In"

        passwordVisibilityHandler()

        binding.btnLogIn.setOnClickListener {
            loginUser()
        }

        binding.createAccount.setOnClickListener {
            startActivity(Intent(this@SignIn_Screen, SignUp_Screen::class.java))
        }

        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this@SignIn_Screen, ForgotPassword_Screen::class.java))
        }
    }


    private fun loginUser() {
        val userEmail = binding.etEmail.text.toString()
        val userPassword = binding.etPassword.text.toString()
        if (userEmail.isNotEmpty() && userPassword.isNotEmpty()
            && Patterns.EMAIL_ADDRESS.matcher(userEmail).matches() && userPassword.length > 7
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(userEmail, userPassword).await()
                    withContext(Dispatchers.Main) {
                        checkLoggedInState()
                        Toast.makeText(this@SignIn_Screen, "Successfully Logged In", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@SignIn_Screen, MainActivity::class.java)).also { finish() }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SignIn_Screen, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else if (userEmail.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
            Toast.makeText(this@SignIn_Screen, "Check your email", Toast.LENGTH_LONG).show()
        else if (userPassword.isEmpty() || userPassword.length < 8)
            Toast.makeText(this@SignIn_Screen, "Check your password", Toast.LENGTH_LONG).show()
    }

    private fun checkLoggedInState() : Boolean {
        // not logged in
        if (auth.currentUser == null) {
            Util.log("You are not logged in")
            return false
        } else {
            Util.log("You are logged in!")
            return true
        }
    }

    override fun onStart() {
        super.onStart()
        if (checkLoggedInState())
            startActivity(Intent(this@SignIn_Screen, MainActivity::class.java)).also { finish() }
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
                            0, 0, R.drawable.visibility_on, 0
                        )
                        //for hide password
                        binding.etPassword.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                        passwordVisible = false
                    } else {
                        //set drawable image here
                        binding.etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.visibility_off, 0
                        )
                        //for show password
                        binding.etPassword.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
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