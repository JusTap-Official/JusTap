package com.binay.shaw.justap.viewModel

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
class SignUp_ViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var status = MutableLiveData<Int>()
    private val errorMessage = MutableLiveData<String>()

    /*
    * 0 - Default
    * 1 - Name missing
    * 2 - Email missing
    * 3 - Password missing
    * 4 - Successful
    * 5 - fail!
    */

    fun getErrorMessage() : String{
        return errorMessage.value.toString()
    }

    init {
        status.value = 0
    }


    fun createNewAccount(nameString: String, emailString: String, passwordString: String) {

        if (nameString.isEmpty()) {
            status.value = 1
        } else if (emailString.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            status.value = 2
        } else if (passwordString.isEmpty() || passwordString.length < 8) {
            status.value = 3
        } else {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(emailString, passwordString).await()
                    withContext(Dispatchers.Main) {
                        if (checkLoggedInState()) {
                            auth.signOut()
                            status.value = 4
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        status.value = 5
                        errorMessage.value = e.message.toString()
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