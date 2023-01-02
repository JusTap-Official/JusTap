package com.binay.shaw.justap.ui.mainScreens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentHistoryBinding
import com.binay.shaw.justap.helper.Util
import com.example.awesomedialog.*


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbarText: TextView
    private lateinit var toolbarClearHistoryButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initialization(container)

        toolbarClearHistoryButton.setOnClickListener {
            clearHistory()
        }


        return binding.root
    }

    private fun clearHistory() {
        AwesomeDialog.build(requireActivity())
            .title(
                "Clear history", ResourcesCompat.getFont(requireContext(), R.font.roboto_medium),
                ContextCompat.getColor(requireContext(), R.color.text_color)
            )
            .body(
                "Are you sure you want to clear history?",
                ResourcesCompat.getFont(requireContext(), R.font.roboto),
                ContextCompat.getColor(requireContext(), R.color.text_color)
            )
            .background(R.drawable.card_drawable)
            .onPositive(
                "Clear",
                R.color.bg_color,
                ContextCompat.getColor(requireContext(), R.color.negative_red)
            ) {
                Toast.makeText(requireContext(), "Positive task", Toast.LENGTH_SHORT).show()
                Util.log("positive")
            }
            .onNegative(
                "Cancel",
                R.color.bg_color,
                ContextCompat.getColor(requireContext(), R.color.text_color)
            ) {
                Toast.makeText(context, "Negative task", Toast.LENGTH_SHORT).show()
                Util.log("negative ")
            }

    }

    private fun initialization(container: ViewGroup?) {

        _binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        toolbarText = binding.root.findViewById(R.id.toolbar_title)
        toolbarText.text = "History"
        toolbarClearHistoryButton = binding.root.findViewById(R.id.rightIcon)
        toolbarClearHistoryButton.visibility = View.VISIBLE

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
