package com.binay.shaw.justap.ui.mainScreens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentQRGeneratorBinding

class QRGeneratorFragment : Fragment() {

    private lateinit var _binding: FragmentQRGeneratorBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initialization(container)

        return binding.root
    }

    private fun initialization(container: ViewGroup?) {

        _binding = FragmentQRGeneratorBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        binding.root.findViewById<TextView>(R.id.toolbar_title)?.text = "Generate QR"

    }
}