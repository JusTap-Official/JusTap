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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.binay.shaw.justap.R
import com.binay.shaw.justap.Util
import com.binay.shaw.justap.databinding.ActivitySignUpScreenBinding
import com.binay.shaw.justap.viewModel.SignUp_ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUp_Screen : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLayout: ConstraintLayout
    private lateinit var buttonText: TextView
    private lateinit var buttonProgress: ProgressBar
    private lateinit var viewModel: SignUp_ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        initialization()

        passwordVisibilityHandler()

        buttonLayout.setOnClickListener {
            if (!Util.checkForInternet(this)) {
                Toast.makeText(this@SignUp_Screen, "You're offline!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            buttonText.visibility = View.GONE
            buttonProgress.visibility = View.VISIBLE
            viewModel.createNewAccount(
                binding.etName.text.toString().trim(),
                binding.etEmail.text.toString().trim(),
                binding.etPassword.text.toString().trim())
        }

        viewModel.status.observe(this) {
            when(it) {
                1 -> {
                    Toast.makeText(this@SignUp_Screen, "Enter name first", Toast.LENGTH_SHORT).show()
                    stopProgress()
                }
                2 -> {
                    Toast.makeText(this@SignUp_Screen, "Check your email", Toast.LENGTH_SHORT).show()
                    stopProgress()
                }
                3 -> {
                    Toast.makeText(this@SignUp_Screen, "Check your password", Toast.LENGTH_SHORT).show()
                    stopProgress()
                }
                4 -> {
                    stopProgress()
                    startActivity(Intent(this@SignUp_Screen, SignIn_Screen::class.java))
                }
                5 -> {
                    stopProgress()
                    Toast.makeText(this@SignUp_Screen, viewModel.getErrorMessage(), Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.loginInstead.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun stopProgress() {
        buttonText.visibility = View.VISIBLE
        buttonProgress.visibility = View.GONE
    }

    private fun initialization() {
        auth = FirebaseAuth.getInstance()
        findViewById<TextView>(R.id.toolbar_title).text = "Create Account"
        buttonLayout = findViewById(R.id.progress_button_bg)
        buttonText = findViewById(R.id.buttonText)
        buttonText.text = "Create a new account"
        buttonProgress = findViewById(R.id.buttonProgress)
        viewModel = ViewModelProvider(this,)[SignUp_ViewModel::class.java]
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