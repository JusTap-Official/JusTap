package com.binay.shaw.justap.ui.mainScreens.appSettingsFragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentEditProfileBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.viewModel.EditProfile_ViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch


class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbarText: TextView
    private lateinit var toolbarBackButton: ImageView
    private lateinit var localUserViewModel: LocalUserViewModel
    private var profilePictureURI: Uri? = null
    private var profileBannerURI: Uri? = null
    private lateinit var localUser: LocalUser
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var editprofileViewmodel: EditProfile_ViewModel
    private var editImageMode = 0   // 0 - Default, 1 - Profile picture, 2 - Banner picture


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        initialization(container)

        toolbarBackButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.cancelChanges.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.confirmChanges.setOnClickListener {

            val inputName = binding.newNameET.text.toString().trim()
            val inputBio = binding.newBioET.text.toString().trim()
            val inputPhone = binding.newPhoneET.text.toString().trim()

            if (!Util.checkForInternet(requireContext())) {
                Toast.makeText(requireContext(), "You're offline!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            editChanges(inputName, inputBio, inputPhone, profilePictureURI, profileBannerURI)

        }

        binding.editBanner.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            editImageMode = 2
            imagePickerActivityResult.launch(galleryIntent)
        }

        binding.editPFP.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            editImageMode = 1
            imagePickerActivityResult.launch(galleryIntent)
        }


        return binding.root
    }

    private fun editChanges(
        inputName: String,
        inputBio: String,
        inputPhone: String,
        profilePictureURI: Uri?,
        profileBannerURI: Uri?,
    ) {

        val originalID = localUser.userID
        val originalEmail = localUser.userEmail
        val originalPhone = localUser.userPhone
        val originalName = localUser.userName
        val originalBio = localUser.userBio
        var originalPFP = localUser.userProfilePicture
        var originalBanner = localUser.userBannerPicture

        if (isInvalidData(inputName, inputBio, inputPhone, profilePictureURI, profileBannerURI))
            return

        val hashMap: MutableMap<String, Any> = HashMap()
        hashMap["userID"] = originalID
        hashMap["email"] = originalEmail

        if (inputName.isNotEmpty())
            hashMap["name"] = inputName
        else hashMap["name"] = originalName


        if (inputBio.isNotEmpty())
            hashMap["bio"] = inputBio
        else if (originalBio?.isNotEmpty() == true)
            hashMap["bio"] = originalBio
        else
            hashMap["bio"] = ""


        if (inputPhone.isNotEmpty())
            hashMap["phone"] = inputPhone
        else if (originalPhone?.isNotEmpty() == true)
            hashMap["phone"] = originalPhone
        else
            hashMap["phone"] = ""

        if (originalPFP.isNullOrEmpty())
            originalPFP = ""

        if (originalBanner.isNullOrEmpty()) {
            originalBanner = ""
        }


        lifecycleScope.launch {
            editprofileViewmodel.updateUser(
                firebaseDatabase, storageRef, originalID,
                hashMap, originalPFP, originalBanner, profilePictureURI,
                profileBannerURI, localUserViewModel)
        }

        editprofileViewmodel.status.observe(viewLifecycleOwner) {
            if (it == 3) {
                Toast.makeText(requireContext(), "Updated Successfully", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

    }

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    if (editImageMode == 1) {
                        profilePictureURI = imageUri
                        binding.profileImage.setImageURI(imageUri)
                    } else {
                        profileBannerURI = imageUri
                        binding.profileBannerIV.setImageURI(imageUri)
                    }
                    editImageMode = 0
                }
            }
        }


    private fun isInvalidData(
        inputName: String,
        inputBio: String,
        inputPhone: String,
        profilePictureURI: Uri?,
        profileBannerURI: Uri?
    ): Boolean {
        if (inputName.isEmpty() && inputBio.isEmpty() && inputPhone.isEmpty() && (profilePictureURI == null ||
            profilePictureURI.toString()
                .isEmpty()) && (profileBannerURI == null || profileBannerURI.toString().isEmpty())
        ) {
            Toast.makeText(requireContext(), "Make changes to update", Toast.LENGTH_SHORT).show()
            return true
        }
        if (inputPhone.isNotEmpty() && inputPhone.length != 10) {
            Toast.makeText(requireContext(), "Phone number must be 10 digits", Toast.LENGTH_SHORT)
                .show()
            return true
        }
        return false
    }


    private fun initialization(container: ViewGroup?) {
        _binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        toolbarText = binding.root.findViewById(R.id.toolbar_title)
        toolbarText.text = "Edit Profile"
        toolbarBackButton = binding.root.findViewById(R.id.leftIcon)
        toolbarBackButton.visibility = View.VISIBLE
        localUserViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[LocalUserViewModel::class.java]
        localUserViewModel.fetchUser.observe(viewLifecycleOwner) {
            localUser = LocalUser(
                it.userID,
                it.userName,
                it.userEmail,
                it.userBio,
                it.userPhone,
                it.userProfilePicture,
                it.userBannerPicture
            )
            binding.newNameET.hint = localUser.userName
            binding.newBioET.hint = localUser.userBio
        }
        storageRef = Firebase.storage.reference
        firebaseDatabase = FirebaseDatabase.getInstance()
        editprofileViewmodel =
            ViewModelProvider(this@EditProfileFragment)[EditProfile_ViewModel::class.java]
    }

}