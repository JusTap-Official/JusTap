package com.binay.shaw.justap.viewModel

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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

    fun getErrorMessage(): String {
        return errorMessage.value.toString()
    }

    init {
        status.value = 0
    }


    fun createNewAccount(nameString: String, emailString: String, passwordString: String) {

        if (nameString.isEmpty()) {
            status.value = 1
        } else if (emailString.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailString)
                .matches()
        ) {
            status.value = 2
        } else if (passwordString.isEmpty() || passwordString.length < 8) {
            status.value = 3
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    auth.createUserWithEmailAndPassword(emailString, passwordString).await()
                    withContext(Dispatchers.Main) {
                        if (checkLoggedInState()) {

                            val firebaseUser = auth.currentUser
                            val uid = firebaseUser?.uid.toString()
                            Util.log("user id is: $uid")
                            //Create user object
                            val user = User(uid, nameString, emailString)
                            Util.log("user is: $user")
                            val db = FirebaseDatabase.getInstance().reference
                            //add user data in the Realtime Database

                            db.child("Users").child(uid).setValue(user).addOnSuccessListener {
                                Util.log("User added Successful")
                                auth.signOut()
                                status.value = 4
                            }
                                .addOnFailureListener {
                                    Util.log("Unsuccessful")
                                    status.value = 5
                                }
                        }

                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        errorMessage.value = e.message.toString()
                        status.value = 5
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