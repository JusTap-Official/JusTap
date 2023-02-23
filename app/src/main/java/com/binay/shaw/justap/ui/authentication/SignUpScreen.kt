package com.binay.shaw.justap.ui.authentication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.databinding.ActivitySignUpScreenBinding
import com.binay.shaw.justap.viewModel.SignUp_ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.tapadoo.alerter.Alerter

@SuppressLint("SetTextI18n")
class SignUpScreen : AppCompatActivity() {

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
                Alerter.create(this@SignUpScreen)
                    .setTitle(resources.getString(R.string.noInternet))
                    .setText(resources.getString(R.string.noInternetDescription))
                    .setBackgroundColorInt(ContextCompat.getColor(baseContext, R.color.negative_red))
                    .setIcon(R.drawable.wifi_off)
                    .setDuration(2000L)
                    .show()
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
                    startActivity(Intent(this@SignUpScreen, SignInScreen::class.java))
                }
                8 -> {
                    Toast.makeText(
                        this@SignUpScreen,
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
                this@SignUpScreen.buttonLayout = this.progressButtonBg
                this@SignUpScreen.buttonText = this.buttonText
                this@SignUpScreen.buttonText.text = resources.getString(R.string.createANewAccount)
                this@SignUpScreen.buttonProgress = this.buttonProgress
            }
        }
        viewModel = ViewModelProvider(this)[SignUp_ViewModel::class.java]
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun passwordVisibilityHandler() {

        // Hide and Show Password
        var passwordVisible = false

        binding.etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility_on, 0)
        binding.etPassword.compoundDrawablePadding = 20 // add padding to increase touch area

        binding.etPassword.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.etPassword.compoundDrawablesRelative[2]
                if (drawableEnd != null && event.x >= binding.etPassword.width - drawableEnd.bounds.width()) {
                    val cursorPosition = binding.etPassword.selectionStart // save current cursor position
                    passwordVisible = !passwordVisible
                    if (passwordVisible) {
                        binding.etPassword.transformationMethod = null
                        binding.etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0)
                    } else {
                        binding.etPassword.transformationMethod = PasswordTransformationMethod()
                        binding.etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility_on, 0)
                    }
                    // Hide the keyboard
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etPassword.windowToken, 0)
                    binding.etPassword.setSelection(cursorPosition)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}