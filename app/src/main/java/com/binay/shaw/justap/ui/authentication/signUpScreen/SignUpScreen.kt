package com.binay.shaw.justap.ui.authentication.signUpScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.databinding.ActivitySignUpScreenBinding
import com.binay.shaw.justap.helper.Util.handlePasswordVisibility
import com.binay.shaw.justap.ui.authentication.signInScreen.SignInScreen
import com.google.firebase.auth.FirebaseAuth


class SignUpScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLayout: ConstraintLayout
    private lateinit var buttonText: TextView
    private lateinit var buttonProgress: ProgressBar
    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)
        supportActionBar?.hide()

        initialization()

        buttonLayout.setOnClickListener {
            if (!Util.checkForInternet(this)) {
                Util.showNoInternet(this)
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
            binding.apply {
                when (it) {
                    1 -> {
                        nameHelperTV.text = getString(R.string.enter_name)
                        nameHelperTV.visibility = View.VISIBLE
                    }
                    2 -> {
                        emailHelperTV.text = getString(R.string.enter_email)
                        emailHelperTV.visibility = View.VISIBLE
                    }
                    3 -> {
                        emailHelperTV.text = getString(R.string.invalid_email)
                        emailHelperTV.visibility = View.VISIBLE
                    }
                    4 -> {
                        passwordHelperTV.text = getString(R.string.enter_password)
                        passwordHelperTV.visibility = View.VISIBLE
                    }
                    5 -> {
                        passwordHelperTV.text = getString(R.string.small_size_password)
                        passwordHelperTV.visibility = View.VISIBLE
                    }
                    6 -> {
                        passwordHelperTV.text = getString(R.string.invalid_password)
                        passwordHelperTV.visibility = View.VISIBLE
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
            etPassword.handlePasswordVisibility(baseContext)
        }
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
    }
}