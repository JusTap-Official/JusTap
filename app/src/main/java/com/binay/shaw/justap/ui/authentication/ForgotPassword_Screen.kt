package com.binay.shaw.justap.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.ActivityForgotPasswordScreenBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ForgotPassword_Screen : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLayout: ConstraintLayout
    private lateinit var buttonText: TextView
    private lateinit var buttonProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialization()

        buttonLayout.setOnClickListener {
            buttonText.visibility = View.GONE
            buttonProgress.visibility = View.VISIBLE
            resetPassword()
        }
    }

    private fun resetPassword() {
        val userEmail = binding.etEmail.text.toString().trim()
        if (userEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Toast.makeText(this@ForgotPassword_Screen, "Check your email", Toast.LENGTH_SHORT).show()
            buttonText.visibility = View.VISIBLE
            buttonProgress.visibility = View.GONE
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.sendPasswordResetEmail(userEmail).await()
                    withContext(Dispatchers.Main) {
                        buttonText.visibility = View.VISIBLE
                        buttonProgress.visibility = View.GONE
                        Toast.makeText(
                            this@ForgotPassword_Screen,
                            "Email has been send",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@ForgotPassword_Screen, SignIn_Screen::class.java))
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        buttonText.visibility = View.VISIBLE
                        buttonProgress.visibility = View.GONE
                        Toast.makeText(this@ForgotPassword_Screen, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun initialization() {
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        findViewById<TextView>(R.id.toolbar_title).text = resources.getString(R.string.LogIn)
        buttonLayout = findViewById(R.id.progress_button_bg)
        buttonText = findViewById(R.id.buttonText)
        buttonText.text = resources.getString(R.string.SendResetLink)
        buttonProgress = findViewById(R.id.buttonProgress)

    }
}