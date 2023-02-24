package com.binay.shaw.justap.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.ActivityForgotPasswordScreenBinding
import com.binay.shaw.justap.databinding.MyToolbarBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.ui.authentication.signInScreen.SignInScreen
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ForgotPasswordScreen : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLayout: ConstraintLayout
    private lateinit var buttonText: TextView
    private lateinit var buttonProgress: ProgressBar
    private lateinit var toolbar: MyToolbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordScreenBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        initialization()

        buttonLayout.setOnClickListener {
            if (Util.checkForInternet(baseContext)) {
                buttonText.visibility = View.GONE
                buttonProgress.visibility = View.VISIBLE
                resetPassword()
            } else {
                Util.showNoInternet(this)
            }
        }
    }

    private fun resetPassword() {
        val userEmail = binding.etEmail.text.toString().trim()
        if (userEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Snackbar.make(binding.root, "Please check your email, it's incorrect", Snackbar.LENGTH_SHORT).show()
            buttonText.visibility = View.VISIBLE
            buttonProgress.visibility = View.GONE
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.sendPasswordResetEmail(userEmail).await()
                    withContext(Dispatchers.Main) {
                        buttonText.visibility = View.VISIBLE
                        buttonProgress.visibility = View.GONE
                        Snackbar.make(binding.root, "We sent an email, please check it", Snackbar.LENGTH_SHORT).show()
                        startActivity(Intent(this@ForgotPasswordScreen, SignInScreen::class.java))
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        buttonText.visibility = View.VISIBLE
                        buttonProgress.visibility = View.GONE
                        Toast.makeText(this@ForgotPasswordScreen, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun initialization() {
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        toolbar = binding.include
        toolbar.toolbarTitle.text = resources.getString(R.string.LogIn)
        binding.btnResetPassword.apply {
            this@ForgotPasswordScreen.buttonText = this.buttonText
            this@ForgotPasswordScreen.buttonLayout = this.progressButtonBg
            this@ForgotPasswordScreen.buttonProgress = this.buttonProgress
            this@ForgotPasswordScreen.buttonText.text = resources.getString(R.string.SendResetLink)
        }
    }
}