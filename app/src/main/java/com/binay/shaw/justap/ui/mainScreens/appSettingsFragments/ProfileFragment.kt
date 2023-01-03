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
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var toolbarText: TextView
    private lateinit var toolbarBackButton: ImageView
    private lateinit var localUserViewModel: LocalUserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialization(container)

        toolbarBackButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        localUserViewModel.bio.observe(viewLifecycleOwner) {
            binding.profileBioTV.text = it.toString()
        }

        localUserViewModel.name.observe(viewLifecycleOwner) {
            binding.profileNameTV.text = it.toString()
        }



        binding.editProfile.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        return binding.root
    }

    private fun initialization(container: ViewGroup?) {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        toolbarText = binding.root.findViewById(R.id.toolbar_title)
        toolbarText.text = "Profile"
        toolbarBackButton = binding.root.findViewById(R.id.leftIcon)
        toolbarBackButton.visibility = View.VISIBLE
        auth = FirebaseAuth.getInstance()
        localUserViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[LocalUserViewModel::class.java]

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}