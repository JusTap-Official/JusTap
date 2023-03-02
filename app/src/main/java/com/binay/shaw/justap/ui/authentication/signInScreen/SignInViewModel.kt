package com.binay.shaw.justap.ui.authentication.signInScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.helper.Constants
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class SignInViewModel(application: Application) : AndroidViewModel(application) {
    var status = MutableLiveData<Int>()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val errorMessage = MutableLiveData<String>()
    var firebaseUser = MutableLiveData<User>()
    var firebaseAccounts = MutableLiveData<List<Accounts>>()
    var googleSignInStatus = MutableLiveData<Boolean>()

    init {
        status.value = 0
    }

    fun getErrorMessage(): String {
        return errorMessage.value.toString()
    }

    fun loginUser(userEmail: String, userPassword: String, firebaseDatabase: DatabaseReference) {

        when (val validate = Util.validateUserAuthInput(null, userEmail, userPassword)) {
            7 -> {
                //Success
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        auth.signInWithEmailAndPassword(userEmail, userPassword).await()
                        if (Util.isUserLoggedIn()) {
                            val userID = FirebaseAuth.getInstance().uid.toString()
                            firebaseDatabase.child(Constants.users).child(userID).get()
                                .addOnSuccessListener {

                                    firebaseUser.value = createUserObject(it)

                                    if (it.hasChild(Constants.accounts)) {
                                        firebaseAccounts.value = createAccountsList(
                                            it.child(
                                                Constants.accounts
                                            )
                                        )
                                    }

                                    Util.log(it.value.toString())
                                    status.value = 7    //Success

                                }.addOnFailureListener {
                                    status.value = 8    //Failed
                                    errorMessage.value = it.message.toString()
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
                if (validate < 7)
                    status.value = validate
            }
        }
    }

    private fun createAccountsList(child: DataSnapshot): MutableList<Accounts> {
        val accountList = mutableListOf<Accounts>()
        child.children.forEach { iterator ->
            val tempMap = iterator.value as java.util.HashMap<*, *>
            val acc = Accounts(
                (tempMap[Constants.accountId] as Long).toInt(),
                tempMap[Constants.accountName] as String,
                tempMap[Constants.accountData] as String,
                tempMap[Constants.showAccount] as Boolean
            )
            accountList.add(acc)
        }
        return accountList
    }

    private fun createUserObject(snapshot: DataSnapshot): User {
        snapshot.apply {
            val id = child(Constants.userID).value.toString()
            val name = child(Constants.name).value.toString()
            val email = child(Constants.email).value.toString()
            val profilePicture = child(Constants.profilePictureUri).value.toString()
            val profileBanner = child(Constants.profileBannerUri).value.toString()
            val bio = child(Constants.bio).value.toString()

            return User(id, name, email, bio, profilePicture, profileBanner)
        }
    }

    fun signInWithGoogle(currentUser: FirebaseUser?) = viewModelScope.launch(Dispatchers.IO) {
        currentUser?.let {
            val userRef = Firebase.database.reference.child("Users").child(it.uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User exists in the database
                        Util.log("User exists")
                        //Get the user
                        val getUser = createUserObject(dataSnapshot)
                        Util.log("get user: $getUser")
                        firebaseUser.postValue(getUser)
                        if (dataSnapshot.hasChild(Constants.accounts)) {
                            firebaseAccounts.value = createAccountsList(
                                dataSnapshot.child(
                                    Constants.accounts
                                )
                            )
                        }
                    } else {
                        // User does not exist in the database
                        Util.log("User does not exist")
                        val saveUser = User(
                            it.uid,
                            it.displayName.toString(),
                            it.email.toString(),
                            "",
                            "",
                            ""
                        )
                        userRef.setValue(saveUser)
                        firebaseUser.postValue(saveUser)
                    }
                    googleSignInStatus.postValue(true)
                }

                override fun onCancelled(error: DatabaseError) {
                    Util.log("getUser:onCancelled ${error.toException()}")
                    googleSignInStatus.postValue(false)
                    errorMessage.postValue(error.message)
                }
            })
        }
    }
}