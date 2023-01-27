package com.binay.shaw.justap.ui.mainScreens.accountFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentAddEditBinding

class AddEditFragment : Fragment() {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbarText: TextView
    private lateinit var toolbarBackButton: ImageView
    private lateinit var toolbarDeleteIcon: ImageView
    private val mode: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initialization(container)






        return binding.root
    }

    private fun initialization(container: ViewGroup?) {
        _binding = FragmentAddEditBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        toolbarText = binding.root.findViewById(R.id.toolbar_title)
        if (mode == 0)
            toolbarText.text = "Add account"
        else {
            toolbarText.text = "Edit account"
            toolbarDeleteIcon = binding.root.findViewById(R.id.rightIcon)
            toolbarDeleteIcon.visibility = View.VISIBLE
        }
        toolbarBackButton = binding.root.findViewById(R.id.leftIcon)
        toolbarBackButton.visibility = View.VISIBLE
    }
}