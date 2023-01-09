package com.binay.shaw.justap.ui.mainScreens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentHomeBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.viewModel.LocalUserViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var localUserViewModel: LocalUserViewModel
    private lateinit var localUser: LocalUser

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialization(container)


        return binding.root
    }

    private fun initialization(container: ViewGroup?) {

        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.root.findViewById<TextView>(R.id.toolbar_title)?.text = requireContext().resources.getString(R.string.Home)
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
            binding.profileNameTV.text  = localUser.userName
            binding.profileBioTV.text = localUser.userBio
            val profileURL = localUser.userProfilePicture!!
            if (profileURL.isNotEmpty()) {
                Util.loadImagesWithGlide(binding.profileImage, profileURL)
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}