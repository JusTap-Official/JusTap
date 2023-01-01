package com.binay.shaw.justap.ui.mainScreens.appSettingsFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialization(container)




        return binding.root
    }

    private fun initialization(container: ViewGroup?) {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        binding.root.findViewById<TextView>(R.id.toolbar_title)?.text = "Profile"
        auth = FirebaseAuth.getInstance()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}