package com.binay.shaw.justap.ui.authentication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
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
    private lateinit var buttonLayout: ConstraintLayout
    private lateinit var buttonText: TextView
    private lateinit var buttonProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialization()

        passwordVisibilityHandler()

        buttonLayout.setOnClickListener {
            buttonText.visibility = View.GONE
            buttonProgress.visibility = View.VISIBLE
            loginUser()
        }

        binding.createAccount.setOnClickListener {
            startActivity(Intent(this@SignIn_Screen, SignUp_Screen::class.java))
        }

        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this@SignIn_Screen, ForgotPassword_Screen::class.java))
        }
    }

    private fun initialization() {
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        findViewById<TextView>(R.id.toolbar_title).text = "Log In"
        buttonLayout = findViewById(R.id.progress_button_bg)
        buttonText = findViewById(R.id.buttonText)
        buttonText.text = "Sign In"
        buttonProgress = findViewById(R.id.buttonProgress)

    }

    private fun loginUser() {
        val userEmail = binding.etEmail.text.toString().trim()
        val userPassword = binding.etPassword.text.toString().trim()

        if (userEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Toast.makeText(this@SignIn_Screen, "Check your email", Toast.LENGTH_SHORT).show()
            buttonText.visibility = View.VISIBLE
            buttonProgress.visibility = View.GONE
        } else if (userPassword.isEmpty() || userPassword.length < 8) {
            Toast.makeText(this@SignIn_Screen, "Check your password", Toast.LENGTH_SHORT).show()
            buttonText.visibility = View.VISIBLE
            buttonProgress.visibility = View.GONE
        } else {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(userEmail, userPassword).await()

                    withContext(Dispatchers.Main) {
//                        checkLoggedInState()
                        buttonText.visibility = View.VISIBLE
                        buttonProgress.visibility = View.GONE
                        Toast.makeText(this@SignIn_Screen, "Successfully Logged In", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignIn_Screen, MainActivity::class.java)).also { finish() }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        buttonText.visibility = View.VISIBLE
                        buttonProgress.visibility = View.GONE
                        Toast.makeText(this@SignIn_Screen, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
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