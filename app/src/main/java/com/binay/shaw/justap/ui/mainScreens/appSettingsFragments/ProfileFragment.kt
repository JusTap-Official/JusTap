package com.binay.shaw.justap.ui.mainScreens.appSettingsFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.binay.shaw.justap.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialization(container)




        return binding.root
    }

    private fun initialization(container: ViewGroup?) {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

    }
}