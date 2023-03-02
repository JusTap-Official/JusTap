package com.binay.shaw.justap.ui.authentication.signUpScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseActivity
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.databinding.ActivitySignUpScreenBinding
import com.binay.shaw.justap.helper.Util.handlePasswordVisibility
import com.binay.shaw.justap.mainViewModels.AccountsViewModel
import com.binay.shaw.justap.mainViewModels.LocalUserViewModel
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.model.User
import com.binay.shaw.justap.ui.authentication.signInScreen.SignInScreen
import com.binay.shaw.justap.ui.authentication.signInScreen.SignInViewModel
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class SignUpScreen : BaseActivity() {

    private lateinit var binding: ActivitySignUpScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLayout: ConstraintLayout
    private lateinit var buttonText: TextView
    private lateinit var buttonProgress: ProgressBar
    private val signUpViewModel by viewModels<SignUpViewModel> { ViewModelFactory() }
    private val signInViewModel by viewModels<SignInViewModel> { ViewModelFactory() }
    private val localUserViewModel by viewModels<LocalUserViewModel> { ViewModelFactory() }
    private val accountsViewModel by viewModels<AccountsViewModel> { ViewModelFactory() }
    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialization()
        initObservers()
        handleOperations()
    }

    private fun handleOperations() {
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
            signUpViewModel.createNewAccount(
                binding.etName.text.toString().trim(),
                binding.etEmail.text.toString().trim(),
                binding.etPassword.text.toString().trim()
            )
        }

        binding.signUpWithGoogle.setOnClickListener {
            signInWithGoogle()
        }

        binding.loginInstead.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initObservers() {
        signUpViewModel.run {
            status.observe(this@SignUpScreen) {
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
                                getErrorMessage(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        signInViewModel.run {
            googleSignInStatus.observe(this@SignUpScreen) {
                when (it) {
                    true -> {
                        Toast.makeText(this@SignUpScreen, "Sign Success", Toast.LENGTH_SHORT).show()
                        Util.log("User: ${signInViewModel.firebaseUser.value}\nAccounts: ${signInViewModel.firebaseAccounts.value}")
                        fetchUserForLogIn()
                    }
                    false -> {
                        Toast.makeText(
                            this@SignUpScreen,
                            "Sign Failed: ${getErrorMessage()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun fetchUserForLogIn() {
        val user = signInViewModel.firebaseUser.value
        val listAccounts = signInViewModel.firebaseAccounts.value
        if (user != null) {
            saveUserLocally(user)
            saveAccountsList(listAccounts)
        }
        Toast.makeText(
            this@SignUpScreen,
            getString(R.string.successfullLoggedIn),
            Toast.LENGTH_SHORT
        ).show()
        startActivity(
            Intent(
                this@SignUpScreen,
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

    private fun saveUserLocally(user: User) {
        val lu = LocalUser(
            user.userID, user.name,
            user.email, user.bio,
            user.profilePictureURI, user.profileBannerURI
        )
        localUserViewModel.insertUser(lu)
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
                    signInViewModel.signInWithGoogle(FirebaseAuth.getInstance().currentUser)
                } else {
                    Util.log("signInWithCredential:failure ${task.exception}")
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
    }
}