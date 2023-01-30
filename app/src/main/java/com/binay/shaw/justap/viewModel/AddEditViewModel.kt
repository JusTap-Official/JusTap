package com.binay.shaw.justap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.LocalUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

/**
 * Created by binay on 27,January,2023
 */
class AddEditViewModel : ViewModel() {

    var status = MutableLiveData<Int>()

    init {
        status.value = 0
    }

    /**
     * 0 - Default
     * 1 - Failed to upload in Firebase
     * 2 - Failed to update Room DB
     * 3 - Success in both
     * */

    suspend fun saveData(
        accountsViewModel: AccountsViewModel,
        firebaseDatabase: FirebaseDatabase,
        userID: String,
        accountID: Int,
        accountName: String,
        accountData: String
    ) = viewModelScope.launch {

        val account = Accounts(accountID, accountName, accountData, true)

        val createNewDataInFirebaseAndRoomDB = launch {

            val createAccountObjectInFirebase = viewModelScope.async(Dispatchers.IO) {
                saveInFirebase(firebaseDatabase, userID, account)
            }

            val createAccountObjectInRoomDB = viewModelScope.async(Dispatchers.IO) {
                saveInRoomDB(accountsViewModel, account)
            }

            status.value = createAccountObjectInFirebase.await() + createAccountObjectInRoomDB.await()

        }

        createNewDataInFirebaseAndRoomDB.join()
        status.value = status.value?.plus(1)



    }


    private suspend fun saveInFirebase(
        firebaseDatabase: FirebaseDatabase,
        userID: String,
        account: Accounts
    ): Int {

        return withContext(Dispatchers.IO) {
            val ref = firebaseDatabase.getReference("/Users/$userID/accounts/${account.accountID}")
            ref.setValue(account).addOnSuccessListener {
                Util.log("Successfully updated data in firebase")
            }.addOnFailureListener {
                Util.log("Failed to update data in firebase")
            }.await()
            1
        }

    }

    suspend fun updateData(
        firebaseDatabase: FirebaseDatabase,
        accountID1: String,
        accountID: Int,
        accountName: String,
        accountData: String
    ) {

    }


    private suspend fun saveInRoomDB(
        accountsViewModel: AccountsViewModel,
        account: Accounts
    ): Int {
        return withContext(Dispatchers.IO) {
            accountsViewModel.insertAccount(account)
            1
        }
    }


}