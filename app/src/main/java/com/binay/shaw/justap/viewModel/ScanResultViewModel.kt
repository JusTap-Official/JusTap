package com.binay.shaw.justap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.LocalHistory
import com.binay.shaw.justap.model.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by binay on 04,February,2023
 * */

class ScanResultViewModel : ViewModel() {

    var showCaseAccountsList = MutableLiveData<List<Accounts>>()
    var scanResultUser = MutableLiveData<User>()
    var status = MutableLiveData<Int>()

    init {
        status.value = 0
    }



    fun getDataFromUserID(userID: String, localUserHistoryViewModel: LocalHistoryViewModel) {

        val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        database.child(userID).get()
            .addOnSuccessListener {
                val name = it.child("name").value.toString()
                val email = it.child("email").value.toString()
                val base64 = it.child("userPFPBase64").value.toString()
                val profilePicture = it.child("profilePictureURI").value.toString()
                val profileBase64 = it.child("userPFPBase64").value.toString()
                val profileBanner = it.child("profileBannerURI").value.toString()
                val bio = it.child("bio").value.toString()

                if (it.hasChild("accounts")) {
                    val accountList = mutableListOf<Accounts>()

                    it.child("accounts").children.forEach { iter ->
                        if (iter != null) {
                            val tempMap = iter.value as java.util.HashMap<*, *>
                            val acc = Accounts(
                                (tempMap["accountID"] as Long).toInt(),
                                tempMap["accountName"] as String,
                                tempMap["accountData"] as String,
                                tempMap["showAccount"] as Boolean
                            )
                            accountList.add(acc)
                        }
                    }

                    showCaseAccountsList.postValue(accountList)
                }

                viewModelScope.launch(Dispatchers.IO) {
                    val localHistory = LocalHistory(userID, name, bio, profileBase64)
                    localUserHistoryViewModel.insertUserHistory(localHistory)
                }

                 scanResultUser.postValue(User(
                    userID, name,
                    email, bio, base64, profilePicture, profileBanner
                ))

                Util.log(it.value.toString())

                status.value = 1    //Success
            }.addOnFailureListener {
                Util.log("Failed to fetch user data")
                status.value = 2    //Failed
            }

    }


    fun getDevelopersAccount() {

        val accounts = mutableListOf<Accounts>()

        accounts.add(Accounts(
            1,
            "Email",
            "binayshaw7777@gmail.com",
            true
        ))
        accounts.add(Accounts(
            3,
            "LinkedIn",
            "https://www.linkedin.com/in/binayshaw7777/",
            true
        ))
        accounts.add(
            Accounts(
                5,
                "Twitter",
                "https://twitter.com/binayplays7777",
                true
            ))
        accounts.add(
            Accounts(
                9,
                "Website",
                "https://binayshaw7777.github.io/BinayShaw.github.io/",
                true
            ))

        showCaseAccountsList.postValue(accounts)
    }

}