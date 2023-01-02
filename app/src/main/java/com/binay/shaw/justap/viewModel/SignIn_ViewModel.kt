package com.binay.shaw.justap.viewModel

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
    var firebaseUser = MutableLiveData<User>()

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

    fun getErrorMessage(): String {
        return errorMessage.value.toString()
    }

    fun loginUser(userEmail: String, userPassword: String, firebaseDatabase: DatabaseReference, localUserDatabase: LocalUserDatabase) {

        if (userEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            status.value = 1
        } else if (userPassword.isEmpty() || userPassword.length < 8) {
            status.value = 2
        } else {

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    auth.signInWithEmailAndPassword(userEmail, userPassword).await()
                    if (checkLoggedInState()) {
                        val userID = FirebaseAuth.getInstance().uid.toString()
                        firebaseDatabase.child("Users").child(userID).get().addOnSuccessListener {
                            val id = it.child("userID").value.toString()
                            val name = it.child("name").value.toString()
                            val email = it.child("email").value.toString()
                            val phone = it.child("phone").value.toString()
                            val profilePicture = it.child("pfpBase64").value.toString()
                            val bio = it.child("bio").value.toString()

                            firebaseUser.value = User(
                                id, name,
                                email, bio, phone, profilePicture
                            )

                            Util.log(it.value.toString())

                            status.value = 3    //Success
                        }.addOnFailureListener {
                            Util.log("Failed to fetch user data")
                            status.value = 4    //Failed
                        }
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

    fun saveData(database: LocalUserDatabase, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            database.localUserDao().insertUser(
                LocalUser(user.userID, user.name,
                user.email, user.bio, user.phone, user.pfpBase64)
            )
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