package com.binay.shaw.justap.ui.mainScreens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentHistoryBinding


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initialization(container)


        return binding.root
    }

    private fun initialization(container: ViewGroup?) {

        _binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        binding.root.findViewById<TextView>(R.id.toolbar_title)?.text = "History"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
//                    findNavController().popBackStack()
                    NavHostFragment.findNavController(this@HistoryFragment).popBackStack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
