package com.binay.shaw.justap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.model.User
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by binay on 02,January,2023
 */
class Home_ViewModel : ViewModel() {

    var status = MutableLiveData<Int>()
    var firebaseUser = MutableLiveData<User>()

    init {
        status.value = 0
        //1 -> success, 2 -> fail
    }

//    fun saveData(database: LocalUserDatabase, user: User) {
//        viewModelScope.launch(Dispatchers.IO) {
//            database.localUserDao().insertUser(LocalUser(user.userID, user.name,
//                user.email, user.bio, user.phone, user.pfpBase64))
//        }
//    }

//    fun fetchUserData(userID: String, database: DatabaseReference) {
//        viewModelScope.launch(Dispatchers.IO) {
//            database.child("Users").child(userID).get().addOnSuccessListener {
//                val id = it.child("userID").value.toString()
//                val name= it.child("name").value.toString()
//                val email = it.child("email").value.toString()
//                val phone = it.child("phone").value.toString()
//                val profilePicture = it.child("pfpBase64").value.toString()
//                val bio = it.child("bio").value.toString()
//
//                firebaseUser.value = User(id, name,
//                    email, bio, phone, profilePicture)
//
//                Util.log(it.value.toString())
//
//                status.value = 1    //Success
//            }.addOnFailureListener {
//                Util.log("Failed to fetch user data")
//                status.value = 2    //Failed
//            }
//        }
}