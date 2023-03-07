package com.binay.shaw.justap.ui.mainScreens.settingsScreen.profileScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentProfileBinding
import com.binay.shaw.justap.databinding.MyToolbarBinding
import com.binay.shaw.justap.helper.Constants
import com.binay.shaw.justap.helper.ImageUtils
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var toolBar: MyToolbarBinding
    private lateinit var localUserViewModel: LocalUserViewModel
    private lateinit var localUser: LocalUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        initialization()
        updateAnalytics()

        toolBar.leftIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.editProfile.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.profileImage.setOnClickListener {
            val imageUrl = localUser.userProfilePicture.toString()
            ImageUtils.showImagePreviewDialog(requireContext(), true, imageUrl, false).show()
        }

        binding.profileBannerIV.setOnClickListener {
            val imageUrl = localUser.userBannerPicture.toString()
            ImageUtils.showImagePreviewDialog(requireContext(), false, imageUrl, false).show()
        }

        return binding.root
    }

    private fun updateAnalytics() {
        val userRef = Firebase.database.reference.child(Constants.users).child(Util.userID)
            .child(Constants.analytics)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists in the database
                    Util.log("Analytics exists")
                    //Get the user
                    if (dataSnapshot.child(Constants.scanCount).exists()) {
                        val scanCount = dataSnapshot.child(Constants.scanCount).value.toString()
                        if (scanCount.isNotEmpty()) {
                            Util.log("Count scan: $scanCount")
                            binding.scanCountTV.text = scanCount
                        }
                    } else {
                        binding.scanCountTV.text = "0"
                    }

                    if (dataSnapshot.child(Constants.impressionCount).exists()) {
                        val impressionCount =
                            dataSnapshot.child(Constants.impressionCount).value.toString()
                        if (impressionCount.isNotEmpty()) {
                            Util.log("Count imp: $impressionCount")
                            binding.impressionCountTV.text = impressionCount
                        }
                    } else {
                        binding.impressionCountTV.text = "0"
                    }

                } else {
                    // User does not exist in the database
                    binding.scanCountTV.text = "0"
                    binding.impressionCountTV.text = "0"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Util.log("getUser:onCancelled ${error.toException()}")
            }
        })
    }

    private fun initialization() {
        (activity as MainActivity).supportActionBar?.hide()

        toolBar = binding.include
        toolBar.toolbarTitle.text = requireContext().resources.getString(R.string.Profile)
        toolBar.leftIcon.visibility = View.VISIBLE
        auth = FirebaseAuth.getInstance()
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
            binding.profileNameTV.text = localUser.userName
            binding.profileBioTV.text = localUser.userBio
            val profileURL = localUser.userProfilePicture.toString()
            val bannerURL = localUser.userBannerPicture.toString()
            if (profileURL.isNotEmpty())
                Util.loadImagesWithGlide(binding.profileImage, profileURL)
            if (bannerURL.isNotEmpty())
                Util.loadImagesWithGlide(binding.profileBannerIV, bannerURL)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}