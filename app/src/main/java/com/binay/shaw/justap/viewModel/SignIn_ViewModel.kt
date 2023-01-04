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

    /** Status values
     * 0 - Default
     * 1 - name is empty
     * 2 - email is empty
     * 3 - email is not valid
     * 4 - password is empty
     * 5 - password is less than 8 characters
     * 6 - Use upper & lowercase with digit & symbols
     * 7 - success
     * 8 - Fail
     * */

    init {
        status.value = 0
    }

    fun getErrorMessage(): String {
        return errorMessage.value.toString()
    }

    fun loginUser(userEmail: String, userPassword: String, firebaseDatabase: DatabaseReference) {

        val checkValidity = Util.validateUserAuthInput(null, userEmail, userPassword)

        when (checkValidity) {
            2 -> {
                //email is empty
                status.value = 2
            }
            3 -> {
                //email is not valid
                status.value = 3
            }
            4 -> {
                //password is empty
                status.value = 4
            }
            5 -> {
                //password is less than 8 characters
                status.value = 5
            }
            6 -> {
                //password must contains Uppercase, lowercase and symbols
                status.value = 6
            }
            7 -> {
                //Success

                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        auth.signInWithEmailAndPassword(userEmail, userPassword).await()
                        if (checkLoggedInState()) {
                            val userID = FirebaseAuth.getInstance().uid.toString()
                            firebaseDatabase.child("Users").child(userID).get()
                                .addOnSuccessListener {
                                    val id = it.child("userID").value.toString()
                                    val name = it.child("name").value.toString()
                                    val email = it.child("email").value.toString()
                                    val phone = it.child("phone").value.toString()
                                    val profilePicture = it.child("profilePictureURI").value.toString()
                                    val profileBanner = it.child("profileBannerURI").value.toString()
                                    val bio = it.child("bio").value.toString()

                                    firebaseUser.value = User(
                                        id, name,
                                        email, bio, phone, profilePicture, profileBanner
                                    )

                                    Util.log(it.value.toString())

                                    status.value = 7    //Success
                                }.addOnFailureListener {
                                    Util.log("Failed to fetch user data")
                                    status.value = 8    //Failed
                                }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            errorMessage.value = e.message.toString()
                            status.value = 8
                        }
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