package com.binay.shaw.justap.viewModel

import android.content.Intent
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.Util
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Created by binay on 31,December,2022
 */
class SignIn_ViewModel : ViewModel() {
    var status = MutableLiveData<Int>()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val errorMessage = MutableLiveData<String>()

    /*
    * 0 - Default
    * 1 - Email missing
    * 2 - Password missing
    * 3 - Successful
    * 4 - fail!
    */

    init {
        status.value = 0
    }

    fun getErrorMessage() : String {
        return errorMessage.value.toString()
    }

    fun loginUser(userEmail: String, userPassword: String) {

        if (userEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            status.value = 1
        } else if (userPassword.isEmpty() || userPassword.length < 8) {
            status.value = 2
        } else {

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    auth.signInWithEmailAndPassword(userEmail, userPassword).await()

                    withContext(Dispatchers.Main) {
                        if (checkLoggedInState())
                            status.value = 3
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        errorMessage.value = e.message.toString()
                        status.value = 4
                    }
                }
            }
        }
    }

    private fun checkLoggedInState(): Boolean {
        // not logged in
        return if (auth.currentUser == null) {
            Util.log("You are not logged in")
            false
        } else {
            Util.log("You are logged in!")
            true
        }
    }
}