package com.binay.shaw.justap.ui.mainScreens.appSettingsFragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentEditProfileBinding
import com.binay.shaw.justap.databinding.MyToolbarBinding
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.Companion.createBottomSheet
import com.binay.shaw.justap.helper.Util.Companion.setBottomSheet
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.viewModel.EditProfile_ViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.tapadoo.alerter.Alerter
import kotlinx.coroutines.launch


class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolBar: MyToolbarBinding
    private lateinit var localUserViewModel: LocalUserViewModel
    private var profilePictureURI: Uri? = null
    private var profileBannerURI: Uri? = null
    private lateinit var localUser: LocalUser
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var editProfileViewmodel: EditProfile_ViewModel
    private var editImageMode = 0   // 0 - Default, 1 - Profile picture, 2 - Banner picture


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        initialization()

        toolBar.leftIcon.setOnClickListener {
            handleBackButtonPress()
        }

        binding.cancelChanges.setOnClickListener {
            handleBackButtonPress()
        }

        binding.confirmChanges.setOnClickListener {

            val inputName = binding.newNameET.text.toString().trim()
            val inputBio = binding.newBioET.text.toString().trim()

            if (!Util.checkForInternet(requireContext())) {
                Alerter.create(requireActivity())
                    .setTitle(resources.getString(R.string.noInternet))
                    .setText(resources.getString(R.string.noInternetDescription))
                    .setBackgroundColorInt(ContextCompat.getColor(requireContext(), R.color.negative_red))
                    .setIcon(R.drawable.wifi_off)
                    .setDuration(2000L)
                    .show()
                return@setOnClickListener
            }
            editChanges(inputName, inputBio, profilePictureURI, profileBannerURI)
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
    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                handleBackButtonPress()
                true
            } else false
        }
    }

    private fun handleBackButtonPress() {
        val inputName = binding.newNameET.text.toString().trim()
        val inputBio = binding.newBioET.text.toString().trim()

        if (inputName.isNotEmpty() || inputBio.isNotEmpty()
            || profileBannerURI != null || profilePictureURI != null) {

            val dialog = OptionsModalBinding.inflate(layoutInflater)
            val bottomSheet = requireContext().createBottomSheet()
            dialog.apply {

                optionsHeading.text = requireContext().resources.getString(R.string.DiscardChanged)
                optionsContent.text = requireContext().resources.getString(R.string.DiscardChangedDescription)
                positiveOption.text = requireContext().resources.getString(R.string.Discard)
                positiveOption.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.negative_red
                    )
                )
                negativeOption.text = requireContext().resources.getString(R.string.ContinueEditing)
                negativeOption.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_color
                    )
                )
                positiveOption.setOnClickListener {
                    bottomSheet.dismiss()
                    Util.log("Go back")
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                negativeOption.setOnClickListener {
                    bottomSheet.dismiss()
                    Util.log("Stay")
                }
            }
            dialog.root.setBottomSheet(bottomSheet)
        }
        else
            requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun editChanges(
        inputName: String,
        inputBio: String,
        profilePictureURI: Uri?,
        profileBannerURI: Uri?,
    ) {

        val originalID = localUser.userID
        val originalEmail = localUser.userEmail
        val originalName = localUser.userName
        val originalBio = localUser.userBio
        var originalPFP = localUser.userProfilePicture
        var originalBanner = localUser.userBannerPicture

        if (isInvalidData(inputName, inputBio, profilePictureURI, profileBannerURI))
            return

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text = requireContext().resources.getString(R.string.ConfirmChanges)
            optionsContent.text = requireContext().resources.getString(R.string.ConfirmChangesDescription)
            positiveOption.text = requireContext().resources.getString(R.string.SaveChanges)
            positiveOption.setTextColor(ContextCompat.getColor(requireContext(), R.color.negative_red))
            negativeOption.text = requireContext().resources.getString(R.string.DontSave)
            negativeOption.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color))
            positiveOption.setOnClickListener {
                bottomSheet.dismiss()
                binding.progressAnimation.progressParent.visibility = View.VISIBLE

                requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                Util.log("Save")

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

                if (originalPFP.isNullOrEmpty())
                    originalPFP = ""

                if (originalBanner.isNullOrEmpty()) {
                    originalBanner = ""
                }

                lifecycleScope.launch {
                    editProfileViewmodel.updateUser(
                        firebaseDatabase, storageRef, originalID,
                        hashMap, originalPFP!!, originalBanner!!, profilePictureURI,
                        profileBannerURI, localUserViewModel)
                }

                editProfileViewmodel.status.observe(viewLifecycleOwner) {
                    if (it == 3) {
                        Glide.get(requireContext()).clearMemory()
                        binding.progressAnimation.progressParent.visibility = View.GONE
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Snackbar.make(binding.root, "Profile updated successfully", Snackbar.LENGTH_SHORT).show()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
                Util.log("Don't Save")
            }
        }
        dialog.root.setBottomSheet(bottomSheet)

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
                        binding.profileBannerIV.scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                    editImageMode = 0
                }
            }
        }


    private fun isInvalidData(
        inputName: String,
        inputBio: String,
        profilePictureURI: Uri?,
        profileBannerURI: Uri?
    ): Boolean {
        if (inputName.isEmpty() && inputBio.isEmpty() && (profilePictureURI == null ||
            profilePictureURI.toString()
                .isEmpty()) && (profileBannerURI == null || profileBannerURI.toString().isEmpty())
        ) {
            Toast.makeText(requireContext(), "Make changes to update", Toast.LENGTH_SHORT).show()
            return true
        }

        return false
    }


    private fun initialization() {
        (activity as MainActivity).supportActionBar?.hide()

        toolBar = binding.include
        toolBar.toolbarTitle.text = requireContext().resources.getString(R.string.EditProfile)
        toolBar.leftIcon.visibility = View.VISIBLE
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
                it.userProfilePicture,
                it.userBannerPicture
            )
            binding.newNameET.hint = localUser.userName
            binding.newBioET.hint = localUser.userBio
            val profileURL = localUser.userProfilePicture.toString()
            val bannerURL = localUser.userBannerPicture.toString()
            if (profileURL.isNotEmpty())
                Util.loadImagesWithGlide(binding.profileImage, profileURL)
            if (bannerURL.isNotEmpty())
                Util.loadImagesWithGlide(binding.profileBannerIV, bannerURL)
        }
        storageRef = Firebase.storage.reference
        firebaseDatabase = FirebaseDatabase.getInstance()
        editProfileViewmodel =
            ViewModelProvider(this@EditProfileFragment)[EditProfile_ViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}