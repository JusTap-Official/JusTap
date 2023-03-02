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
import com.binay.shaw.justap.helper.Constants
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SignInScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySignInScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLayout: ConstraintLayout
    private lateinit var buttonText: TextView
    private lateinit var buttonProgress: ProgressBar
    private lateinit var viewModel: SignInViewModel
    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var localDatabase: LocalUserDatabase
    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInScreenBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        initialization()

        observeViewModelResult()
        initViews()

    }

    private fun initViews() {
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

        binding.signInWithGoogle.setOnClickListener {
            signInWithGoogle()
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
                    viewModel.signInWithGoogle(FirebaseAuth.getInstance().currentUser)
                } else {
                    Util.log("signInWithCredential:failure ${task.exception}")
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun observeViewModelResult() {
        viewModel.run {
            status.observe(this@SignInScreen) {
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

            googleSignInStatus.observe(this@SignInScreen) {
                when (it) {
                    true -> {
                        Toast.makeText(this@SignInScreen, "Sign Success", Toast.LENGTH_SHORT).show()
                        Util.log("User: ${viewModel.firebaseUser.value}\nAccounts: ${viewModel.firebaseAccounts.value}")
                        fetchUserForLogIn()
                    }
                    false -> {
                        Toast.makeText(
                            this@SignInScreen,
                            "Sign Failed: ${getErrorMessage()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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