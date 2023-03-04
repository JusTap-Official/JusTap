package com.binay.shaw.justap.ui.authentication.signInScreen


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseActivity
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.databinding.ActivitySignInScreenBinding
import com.binay.shaw.justap.helper.Util.handlePasswordVisibility
import com.binay.shaw.justap.ui.authentication.ForgotPasswordScreen
import com.binay.shaw.justap.ui.authentication.signUpScreen.SignUpScreen
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.viewModel.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*


class SignInScreen : BaseActivity() {

    private lateinit var binding: ActivitySignInScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLayout: ConstraintLayout
    private lateinit var buttonText: TextView
    private lateinit var buttonProgress: ProgressBar
    private val RC_SIGN_IN = 100
    private val accountsViewModel by viewModels<AccountsViewModel> { ViewModelFactory() }
    private val localUserViewModel by viewModels<LocalUserViewModel> { ViewModelFactory() }
    private val firebaseViewModel by viewModels<FirebaseViewModel> { ViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservers()
        initViews()
        handleOperations()
    }

    private fun handleOperations() {
        buttonLayout.setOnClickListener {
            if (!Util.checkForInternet(this)) {
                Util.showNoInternet(this)
                return@setOnClickListener
            }
            showProgress()

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val validation = Util.validateUserAuthInput(null, email, password)
            if (validation < 7) {
                stopProgress()
                handleErrorInput(validation)
                return@setOnClickListener
            }

            firebaseViewModel.logInUser(
                email, password
            )

        }

        binding.createAccount.setOnClickListener {
            startActivity(Intent(this@SignInScreen, SignUpScreen::class.java))
        }

        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this@SignInScreen, ForgotPasswordScreen::class.java))
        }

        binding.signInWithGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun handleErrorInput(validationCode: Int) {
        binding.apply {
            when (validationCode) {
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

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Util.log("Google sign in failed $e")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    firebaseViewModel.signInWithGoogle(FirebaseAuth.getInstance().currentUser)
                } else {
                    Util.log("signInWithCredential:failure ${task.exception}")
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun initObservers() {
        firebaseViewModel.run {
            userLiveData.observe(this@SignInScreen) {
                fetchUserForLogIn()
            }
            errorLiveData.observe(this@SignInScreen) {
                Toast.makeText(
                    this@SignInScreen,
                    "Sign Failed: $it",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun fetchUserForLogIn() {
        val user = firebaseViewModel.userLiveData.value
        val listAccounts = firebaseViewModel.accountsLiveData.value

        if (user != null) {
            localUserViewModel.insertUser(user)
            saveAccountsList(listAccounts)
        }

        startActivity(
            Intent(
                this@SignInScreen,
                MainActivity::class.java
            )
        ).also { finish() }
    }

    private fun saveAccountsList(listAccounts: List<Accounts>?) {
        if (listAccounts != null) {
            for (singleAccount in listAccounts) {
                accountsViewModel.insertAccount(singleAccount)
            }
        }
    }

    private fun showProgress() {
        buttonText.visibility = View.GONE
        buttonProgress.visibility = View.VISIBLE
        binding.emailHelperTV.visibility = View.GONE
        binding.passwordHelperTV.visibility = View.GONE
    }

    private fun stopProgress() {
        buttonText.visibility = View.VISIBLE
        buttonProgress.visibility = View.GONE
    }

    private fun initViews() {
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
    }
}