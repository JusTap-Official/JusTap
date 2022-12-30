package com.binay.shaw.justap.ui.mainScreens

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentQRScannerBinding
import com.binay.shaw.justap.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class ScannerFragment : Fragment() {

    private lateinit var _binding: FragmentQRScannerBinding
    private val binding get() = _binding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQRScannerBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }
}