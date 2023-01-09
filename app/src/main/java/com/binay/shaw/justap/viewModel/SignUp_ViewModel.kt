package com.binay.shaw.justap.viewModel

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

    fun getErrorMessage(): String {
        return errorMessage.value.toString()
    }

    init {
        status.value = 0
    }


    fun createNewAccount(nameString: String, emailString: String, passwordString: String) {

        val isValid = Util.validateUserAuthInput(nameString, emailString, passwordString)
        when (isValid) {

            1 -> status.value = 1   //name is empty
            2 -> status.value = 2   //email is empty
            3 -> status.value = 3   //email is not valid
            4 -> status.value = 4   //password is empty
            5 -> status.value = 5   //password is less than 8 characters
            6 -> status.value = 6   //Use upper & lowercase with digit & symbols
            7 -> {  //success
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
                                    status.value = 7
                                }
                                    .addOnFailureListener {
                                        Util.log("Unsuccessful")
                                        status.value = 8
                                    }
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