package com.binay.shaw.justap.ui.mainScreens.appSettingsFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentProfileBinding
import com.binay.shaw.justap.databinding.MyToolbarBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.google.firebase.auth.FirebaseAuth


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

        toolBar.leftIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.editProfile.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        return binding.root
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