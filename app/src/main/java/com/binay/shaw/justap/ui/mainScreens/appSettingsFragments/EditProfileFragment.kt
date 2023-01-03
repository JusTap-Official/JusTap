package com.binay.shaw.justap.ui.mainScreens.appSettingsFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentEditProfileBinding
import com.binay.shaw.justap.viewModel.LocalUserViewModel

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbarText: TextView
    private lateinit var toolbarBackButton: ImageView
    private lateinit var localUserViewModel: LocalUserViewModel
    private lateinit var userName: StringBuilder
    private lateinit var userBio: StringBuilder
    private lateinit var userBannerBase64: StringBuilder
    private lateinit var userProfileBase64: StringBuilder

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
            editChanges()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        localUserViewModel.name.observe(viewLifecycleOwner) {
            userName.setLength(0)
            userName.append(it)
            binding.newNameET.hint = userName
        }

        localUserViewModel.bio.observe(viewLifecycleOwner) {
            if (it.toString().isNotEmpty()) {
                userBio.setLength(0)
                userBio.append(it)
                binding.newBioET.hint = userBio
            }
        }

        binding.confirmChanges.setOnClickListener {
            val inputName = binding.newNameET.text.toString().trim()
            val inputBio = binding.newBioET.text.toString().trim()
//            if (checkValidChanges(inputName, inputBio)) {
//
//
//
//            }
        }



        // Inflate the layout for this fragment
        return binding.root
    }

    private fun checkValidChanges(inputName: String, inputBio: String) : Boolean{
        if (inputName.isEmpty() && inputBio.isEmpty())
            return false
        else if ((inputName.equals(userName)) || (inputBio.equals(userBio)))
            return false
        return true
    }

    private fun editChanges() {
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
        userName = StringBuilder()
        userBio = StringBuilder()
        userProfileBase64 = StringBuilder()
        userBannerBase64 = StringBuilder()

    }

}