package com.binay.shaw.justap.ui.authentication.signInScreen


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
import androidx.room.Room
import com.binay.shaw.justap.Constants
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.databinding.ActivitySignInScreenBinding
import com.binay.shaw.justap.helper.Util.handlePasswordVisibility
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.ui.authentication.ForgotPasswordScreen
import com.binay.shaw.justap.ui.authentication.signUpScreen.SignUpScreen
import com.binay.shaw.justap.mainViewModels.AccountsViewModel
import com.binay.shaw.justap.mainViewModels.LocalUserViewModel
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignInScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySignInScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLayout: ConstraintLayout
    private lateinit var buttonText: TextView
    private lateinit var buttonProgress: ProgressBar
    private lateinit var viewModel: SignInViewModel
    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var localDatabase: LocalUserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInScreenBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        initialization()

        observeViewModelResult()

        buttonLayout.setOnClickListener {
            if (!Util.checkForInternet(this)) {
                Util.showNoInternet(this)
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

        binding.createAccount.setOnClickListener {
            startActivity(Intent(this@SignInScreen, SignUpScreen::class.java))
        }

        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this@SignInScreen, ForgotPasswordScreen::class.java))
        }
    }

    private fun observeViewModelResult() {
        viewModel.status.observe(this@SignInScreen) {
            stopProgress()
            when (it) {
                2 -> {
                    binding.emailHelperTV.text = getString(R.string.enter_email)
                    binding.emailHelperTV.visibility = View.VISIBLE
                }
                3 -> {
                    binding.emailHelperTV.text = getString(R.string.invalid_email)
                    binding.emailHelperTV.visibility = View.VISIBLE
                }
                4 -> {
                    binding.passwordHelperTV.text = getString(R.string.enter_password)
                    binding.passwordHelperTV.visibility = View.VISIBLE
                }
                5 -> {
                    binding.passwordHelperTV.text = getString(R.string.small_size_password)
                    binding.passwordHelperTV.visibility = View.VISIBLE
                }
                6 -> {
                    binding.passwordHelperTV.text = getString(R.string.invalid_password)
                    binding.passwordHelperTV.visibility = View.VISIBLE
                }
                7 -> {
                    fetchUserForLogIn()
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
    }

    private fun fetchUserForLogIn() {
        val user = viewModel.firebaseUser.value
        val listAccounts = viewModel.firebaseAccounts.value
        if (user != null) {
            saveUserLocally(user)
            saveAccountsList(listAccounts)
        }
        Toast.makeText(this@SignInScreen, getString(R.string.successfullLoggedIn), Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(
                this@SignInScreen,
                MainActivity::class.java
            )
        ).also { finish() }
    }

    private fun saveAccountsList(listAccounts: List<Accounts>?) {
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

    private fun saveUserLocally(user: User) {
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
    }

    private fun stopProgress() {
        buttonText.visibility = View.VISIBLE
        buttonProgress.visibility = View.GONE
    }

    private fun initialization() {
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        binding.apply {
            include.toolbarTitle.text = getString(R.string.LogIn)
            btnLogIn.apply {
                this@SignInScreen.buttonLayout = this.progressButtonBg
                this@SignInScreen.buttonText = this.buttonText
                this@SignInScreen.buttonText.text = getString(R.string.signin)
                this@SignInScreen.buttonProgress = this.buttonProgress
            }
            etPassword.handlePasswordVisibility(baseContext)
        }
        viewModel = ViewModelProvider(this@SignInScreen)[SignInViewModel::class.java]
        firebaseDatabase = FirebaseDatabase.getInstance().reference
        localDatabase = Room.databaseBuilder(
            applicationContext, LocalUserDatabase::class.java,
            Constants.localDB
        ).build()
    }
}