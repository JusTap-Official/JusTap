package com.binay.shaw.justap.ui.authentication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.databinding.ActivitySignInScreenBinding
import com.binay.shaw.justap.viewModel.SignIn_ViewModel
import com.google.firebase.auth.FirebaseAuth

class SignIn_Screen : AppCompatActivity() {

    private lateinit var binding: ActivitySignInScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLayout: ConstraintLayout
    private lateinit var buttonText: TextView
    private lateinit var buttonProgress: ProgressBar
    private lateinit var viewModel: SignIn_ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialization()

        passwordVisibilityHandler()

        buttonLayout.setOnClickListener {
            if (!Util.checkForInternet(this)) {
                Toast.makeText(this@SignIn_Screen, "You're offline!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            buttonText.visibility = View.GONE
            buttonProgress.visibility = View.VISIBLE
            viewModel.loginUser(
                binding.etEmail.text.toString().trim(),
                binding.etPassword.text.toString().trim()
            )

        }

        viewModel.status.observe(this@SignIn_Screen) {
            when(it) {
                1 -> {
                    Toast.makeText(this@SignIn_Screen, "Check your email", Toast.LENGTH_SHORT).show()
                    stopProgress()
                } 2 -> {
                    Toast.makeText(this@SignIn_Screen, "Check your password", Toast.LENGTH_SHORT).show()
                    stopProgress()
                } 3 -> {
                    stopProgress()
                    Toast.makeText(this@SignIn_Screen, "Successfully Logged In", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignIn_Screen, MainActivity::class.java)).also { finish() }
                } 4 -> {
                    stopProgress()
                    Toast.makeText(this@SignIn_Screen, viewModel.getErrorMessage(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.createAccount.setOnClickListener {
            startActivity(Intent(this@SignIn_Screen, SignUp_Screen::class.java))
        }

        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this@SignIn_Screen, ForgotPassword_Screen::class.java))
        }
    }

    private fun stopProgress() {
        buttonText.visibility = View.VISIBLE
        buttonProgress.visibility = View.GONE
    }

    private fun initialization() {
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        findViewById<TextView>(R.id.toolbar_title).text = "Log In"
        buttonLayout = findViewById(R.id.progress_button_bg)
        buttonText = findViewById(R.id.buttonText)
        buttonText.text = "Sign In"
        buttonProgress = findViewById(R.id.buttonProgress)
        viewModel = ViewModelProvider(this@SignIn_Screen)[SignIn_ViewModel::class.java]

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