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
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.databinding.ActivitySignUpScreenBinding
import com.binay.shaw.justap.viewModel.SignUp_ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("SetTextI18n")
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
                Snackbar.make(binding.root, "No Internet available", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            buttonText.visibility = View.GONE
            buttonProgress.visibility = View.VISIBLE
            binding.passwordHelperTV.visibility = View.GONE
            binding.nameHelperTV.visibility = View.GONE
            binding.emailHelperTV.visibility = View.GONE
            viewModel.createNewAccount(
                binding.etName.text.toString().trim(),
                binding.etEmail.text.toString().trim(),
                binding.etPassword.text.toString().trim()
            )
        }

        viewModel.status.observe(this) {
            stopProgress()
            when (it) {
                1 -> {
                    binding.nameHelperTV.text = "Enter your name"
                    binding.nameHelperTV.visibility = View.VISIBLE
                }
                2 -> {
                    binding.emailHelperTV.text = "Enter your email"
                    binding.emailHelperTV.visibility = View.VISIBLE
                }
                3 -> {
                    binding.emailHelperTV.text = "Your email is not valid"
                    binding.emailHelperTV.visibility = View.VISIBLE
                }
                4 -> {
                    binding.passwordHelperTV.text = "Enter your password"
                    binding.passwordHelperTV.visibility = View.VISIBLE
                }
                5 -> {
                    binding.passwordHelperTV.text = "Password length less than 8 letters"
                    binding.passwordHelperTV.visibility = View.VISIBLE
                }
                6 -> {
                    binding.passwordHelperTV.text = "Password must contains uppercase, lowercase, digit and symbol"
                    binding.passwordHelperTV.visibility = View.VISIBLE
                }
                7 -> {
                    startActivity(Intent(this@SignUp_Screen, SignIn_Screen::class.java))
                }
                8 -> {
                    Toast.makeText(
                        this@SignUp_Screen,
                        viewModel.getErrorMessage(),
                        Toast.LENGTH_SHORT
                    ).show()
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
        binding.apply {
            include.toolbarTitle.text = resources.getString(R.string.createAccount)
            btnCreateAccount.apply {
                this@SignUp_Screen.buttonLayout = this.progressButtonBg
                this@SignUp_Screen.buttonText = this.buttonText
                this@SignUp_Screen.buttonText.text = resources.getString(R.string.createANewAccount)
                this@SignUp_Screen.buttonProgress = this.buttonProgress
            }
        }
        viewModel = ViewModelProvider(this)[SignUp_ViewModel::class.java]
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