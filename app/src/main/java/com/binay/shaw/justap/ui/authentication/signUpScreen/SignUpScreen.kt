package com.binay.shaw.justap.ui.authentication.signUpScreen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseActivity
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.databinding.ActivitySignUpScreenBinding
import com.binay.shaw.justap.helper.Util.handlePasswordVisibility
import com.binay.shaw.justap.ui.authentication.signInScreen.SignInScreen
import com.binay.shaw.justap.viewModel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth


class SignUpScreen : BaseActivity() {

    private lateinit var binding: ActivitySignUpScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLayout: ConstraintLayout
    private lateinit var buttonText: TextView
    private lateinit var buttonProgress: ProgressBar
    private val firebaseViewModel by viewModels<FirebaseViewModel> { ViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initObservers()
        handleOperations()
    }

    private fun handleOperations() {
        buttonLayout.setOnClickListener {
            if (!Util.checkForInternet(this)) {
                Util.showNoInternet(this)
                return@setOnClickListener
            }
            showProgress()

            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val validation = Util.validateUserAuthInput(name, email, password)
            if (validation < 7) {
                stopProgress()
                handleErrorInput(validation)
                return@setOnClickListener
            }

            firebaseViewModel.createNewAccount(
                name, email, password
            )
        }

        binding.loginInstead.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showProgress() {
        buttonText.visibility = View.GONE
        buttonProgress.visibility = View.VISIBLE
        binding.passwordHelperTV.visibility = View.GONE
        binding.nameHelperTV.visibility = View.GONE
        binding.emailHelperTV.visibility = View.GONE
    }

    private fun initObservers() {
        firebaseViewModel.run {
            registerStatus.observe(this@SignUpScreen) {
                if (it)
                    startActivity(Intent(this@SignUpScreen, SignInScreen::class.java))
            }
            errorLiveData.observe(this@SignUpScreen) {
                Toast.makeText(
                    this@SignUpScreen,
                    it,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handleErrorInput(validationCode: Int) {
        binding.apply {
            when (validationCode) {
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
            }
        }
    }

    private fun stopProgress() {
        buttonText.visibility = View.VISIBLE
        buttonProgress.visibility = View.GONE
    }

    private fun initViews() {
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
    }
}