package com.binay.shaw.justap.viewModel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.LocalUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by binay on 04,January,2023
 */
class EditProfile_ViewModel : ViewModel() {

    var status = MutableLiveData<Int>()

    init {
        status.value = 0
    }

    /**
     * 0 - Default
     * 1 - Pfp success
     * 2 - pfp fail
     * 3 - banner success
     * 4 - banner fail
     * 5 - firebaseUserUpload success
     * 6 - firebaseUserUpload fail
     * */

    fun updateUserProfile(
        firebaseDatabase: FirebaseDatabase,
        storageRef: StorageReference,
        originalID: String,
        userMap: MutableMap<String, Any>,
        originalProfilePicture: String,
        originalBannerPicture: String,
        profilePictureURI: Uri?,
        profileBannerURI: Uri?,
        localUserViewModel: LocalUserViewModel
    ) {


//        viewModelScope.launch(Dispatchers.IO) {


        //To upload profile picture
        if (profilePictureURI != null) {

            val uploadTask =
                storageRef.child("file/profilePicture/$originalID").putFile(profilePictureURI)

            uploadTask.addOnSuccessListener { it ->
                Util.log("Profile image Upload success")

                val result = it.metadata!!.reference!!.downloadUrl
                result.addOnSuccessListener { it1 ->

                    val imageLink = it1.toString()
                    userMap["profilePictureURI"] = imageLink
//                        firebaseDatabase.getReference("/Users/$originalID/profilePictureURI")
//                            .setValue(imageLink)
//                            .addOnSuccessListener {
//                                Util.log("Profile image Upload success with link: $imageLink")
//                            }.addOnFailureListener {
//                                Util.log("Profile image Upload fail in Firebase RealTime Database")
//                            }
//
//
//                    }.addOnFailureListener {
//                        Util.log("Profile image Upload fail in Firebase Storage")
//                    }
                }
            }
        } else
            userMap["profilePictureURI"] = originalProfilePicture


        //To upload profile banner
        if (profileBannerURI != null) {
            val uploadTask =
                storageRef.child("file/profileBanner/$originalID").putFile(profileBannerURI)

            uploadTask.addOnSuccessListener { it ->
                Util.log("Banner image uploaded")

                val result = it.metadata!!.reference!!.downloadUrl
                result.addOnSuccessListener {

                    val imageLink = it.toString()
                    userMap["profileBannerURI"] = imageLink

//                    firebaseDatabase.getReference("/Users/$originalID/profileBannerURI")
//                        .setValue(imageLink)
//                        .addOnSuccessListener {
//                            Util.log("Profile banner Upload success with link: $imageLink")
//                        }.addOnFailureListener {
//                            Util.log("Profile banner Upload failed in Firebase RealTime Database")
//                        }
                }
            }.addOnFailureListener {
                Util.log("Banner image Upload fail in Firebase Storage")
            }
        } else
            userMap["profileBannerURI"] = originalProfilePicture


        val ref = firebaseDatabase.getReference("/Users/$originalID")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.ref.updateChildren(userMap)
                Util.log("Successfully updated data in firebase")
            }

            override fun onCancelled(error: DatabaseError) {
                Util.log("Failed to update data in firebase")
            }
        })


        val localUser = LocalUser(
            userMap["userID"].toString(),
            userMap["name"].toString(),
            userMap["email"].toString(),
            userMap["bio"].toString(),
            userMap["phone"].toString(),
            userMap["profilePictureURI"].toString(),
            userMap["profileBannerURI"].toString(),
        )
        localUserViewModel.updateUser(localUser)
    }
}


//}