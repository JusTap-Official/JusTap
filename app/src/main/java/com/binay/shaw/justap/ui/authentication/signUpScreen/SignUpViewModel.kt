package com.binay.shaw.justap.ui.authentication.signUpScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.helper.Constants
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class SignUpViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var status = MutableLiveData<Int>()
    private val errorMessage = MutableLiveData<String>()

    fun getErrorMessage(): String {
        return errorMessage.value.toString()
    }

    init {
        status.value = 0
    }

    fun createNewAccount(nameString: String, emailString: String, passwordString: String) {

        when (val isValid = Util.validateUserAuthInput(nameString, emailString, passwordString)) {

            7 -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        auth.createUserWithEmailAndPassword(emailString, passwordString).await()
                        withContext(Dispatchers.Main) {
                            if (Util.isUserLoggedIn()) {

                                val firebaseUser = auth.currentUser

                                val uid = firebaseUser?.uid.toString()

                                val user = User(uid, nameString, emailString)

                                FirebaseDatabase.getInstance().reference.child(Constants.users)
                                    .child(uid).setValue(user).addOnSuccessListener {
                                    auth.signOut()
                                    status.value = 7

                                }.addOnFailureListener {
                                    errorMessage.value = it.message.toString()
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
            else -> {
                if (isValid < 7)
                    status.value = isValid
            }
        }
    }
}