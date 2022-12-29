package com.binay.shaw.justap.ui.authentication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import com.binay.shaw.justap.R
import com.binay.shaw.justap.Util
import com.binay.shaw.justap.databinding.ActivitySignUpScreenBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUp_Screen : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        findViewById<TextView>(R.id.toolbar_title).text = "Create Account"

        passwordVisibilityHandler()

        binding.btnCreateAccount.setOnClickListener {
            createNewAccount()
        }

        binding.loginInstead.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun createNewAccount() {
        val userName = binding.etName.text.toString()
        val userEmail = binding.etEmail.text.toString()
        val userPassword = binding.etPassword.text.toString()

        if (userName.isNotEmpty() && userEmail.isNotEmpty() && userPassword.isNotEmpty()
            && Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            if (userPassword.length > 7) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.createUserWithEmailAndPassword(userEmail, userPassword).await()
                        withContext(Dispatchers.Main) {
                            if (checkLoggedInState()) {
                                auth.signOut()
                                Toast.makeText(this@SignUp_Screen, "Successfully Registered", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@SignUp_Screen, SignIn_Screen::class.java))
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SignUp_Screen, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

    }

    private fun checkLoggedInState() : Boolean{
        // not logged in
        return if (auth.currentUser == null) {
            Util.log("You are not logged in")
            false
        } else {
            Util.log("You are logged in!")
            true
        }
    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun passwordVisibilityHandler() {
        // Hide and Show Password
        var passwordVisible = false
        binding.etPassword.setOnTouchListener { _, event ->
            val right = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.etPassword.right - binding.etPassword.compoundDrawables[right].bounds.width()
                ) {
                    val selection: Int = binding.etPassword.selectionEnd
                    //Handles Multiple option popups
                    if (passwordVisible) {
                        //set drawable image here
                        binding.etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.visibility_on, 0)
                        //for hide password
                        binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                        passwordVisible = false
                    } else {
                        //set drawable image here
                        binding.etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.visibility_off, 0)
                        //for show password
                        binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        passwordVisible = true
                    }
                    binding.etPassword.isLongClickable = false //Handles Multiple option popups
                    binding.etPassword.setSelection(selection)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}