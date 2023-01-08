package com.binay.shaw.justap.ui.mainScreens.appSettingsFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentAboutMeBinding

class AboutMeFragment : Fragment() {

    private var _binding: FragmentAboutMeBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbarText: TextView
    private lateinit var toolbarBackButton: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initialization(container)

        toolbarBackButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }



        return binding.root
    }

    private fun initialization(container: ViewGroup?) {
        _binding = FragmentAboutMeBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        toolbarText = binding.root.findViewById(R.id.toolbar_title)
        toolbarText.text = "About me"
        toolbarBackButton = binding.root.findViewById(R.id.leftIcon)
        toolbarBackButton.visibility = View.VISIBLE

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}