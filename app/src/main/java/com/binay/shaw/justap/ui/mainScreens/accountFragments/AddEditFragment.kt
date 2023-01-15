package com.binay.shaw.justap.ui.mainScreens.accountFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentAddEditBinding

class AddEditFragment : Fragment() {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initialization(container)






        return binding.root
    }

    private fun initialization(container: ViewGroup?) {
        _binding = FragmentAddEditBinding.inflate(layoutInflater, container, false)

    }
}