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
import androidx.room.Room
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.databinding.ActivitySignInScreenBinding
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.binay.shaw.justap.viewModel.SignIn_ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tapadoo.alerter.Alerter

@SuppressLint("SetTextI18n")
class SignInScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySignInScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLayout: ConstraintLayout
    private lateinit var buttonText: TextView
    private lateinit var buttonProgress: ProgressBar
    private lateinit var viewModel: SignIn_ViewModel
    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var localDatabase: LocalUserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialization()

        passwordVisibilityHandler()

        buttonLayout.setOnClickListener {
            if (!Util.checkForInternet(this)) {
                Alerter.create(this@SignInScreen)
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
            binding.emailHelperTV.visibility = View.GONE
            binding.passwordHelperTV.visibility = View.GONE
            viewModel.loginUser(
                binding.etEmail.text.toString().trim(),
                binding.etPassword.text.toString().trim(),
                firebaseDatabase
            )

        }

        viewModel.status.observe(this@SignInScreen) {
            stopProgress()
            when (it) {
                2 -> {
                    binding.emailHelperTV.text = "Enter your email"
                    binding.emailHelperTV.visibility = View.VISIBLE
                }
                3 -> {
                    binding.emailHelperTV.text = "Email is not valid"
                    binding.emailHelperTV.visibility = View.VISIBLE
                }
                4 -> {
                    binding.passwordHelperTV.text = "Password is empty"
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
                    val user = viewModel.firebaseUser.value
                    val listAccounts = viewModel.firebaseAccounts.value
                    if (user != null) {
                        val localUserViewModel: LocalUserViewModel =
                            ViewModelProvider(
                                this@SignInScreen,
                                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                            )[LocalUserViewModel::class.java]
                        val lu = LocalUser(
                            user.userID, user.name,
                            user.email, user.bio,
                            user.profilePictureURI, user.profileBannerURI
                        )
                        localUserViewModel.insertUser(lu)


                        val accountsViewModel: AccountsViewModel =
                            ViewModelProvider(
                                this@SignInScreen,
                                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                            )[AccountsViewModel::class.java]
                        if (listAccounts != null) {
                            for (singleAccount in listAccounts) {
                                accountsViewModel.insertAccount(singleAccount)
                            }
                        }
                    }
                    Snackbar.make(binding.root, "Successfully Logged In", Snackbar.LENGTH_SHORT).show()
                    startActivity(
                        Intent(
                            this@SignInScreen,
                            MainActivity::class.java
                        )
                    ).also { finish() }
                }
                8 -> {
                    Toast.makeText(
                        this@SignInScreen,
                        viewModel.getErrorMessage(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.createAccount.setOnClickListener {
            startActivity(Intent(this@SignInScreen, SignUpScreen::class.java))
        }

        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this@SignInScreen, ForgotPasswordScreen::class.java))
        }
    }

    private fun stopProgress() {
        buttonText.visibility = View.VISIBLE
        buttonProgress.visibility = View.GONE
    }

    private fun initialization() {
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        binding.apply {
            include.toolbarTitle.text = "Log In"
            btnLogIn.apply {
                this@SignInScreen.buttonLayout = this.progressButtonBg
                this@SignInScreen.buttonText = this.buttonText
                this@SignInScreen.buttonText.text = "Sign In"
                this@SignInScreen.buttonProgress = this.buttonProgress
            }
        }
        viewModel = ViewModelProvider(this@SignInScreen)[SignIn_ViewModel::class.java]
        firebaseDatabase = FirebaseDatabase.getInstance().reference
        localDatabase = Room.databaseBuilder(
            applicationContext, LocalUserDatabase::class.java,
            "localDB"
        ).build()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun passwordVisibilityHandler() {

        // Hide and Show Password
        var passwordVisible = false

        binding.etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility_on, 0)
        binding.etPassword.compoundDrawablePadding = 16 // add padding to increase touch area

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
                    binding.etPassword.setSelection(cursorPosition)

                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etPassword.windowToken, 0)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}