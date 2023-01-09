package com.binay.shaw.justap.ui.mainScreens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentHistoryBinding
import com.binay.shaw.justap.databinding.OptionsDialogBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.Companion.createBottomSheet
import com.binay.shaw.justap.helper.Util.Companion.setBottomSheet


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbarText: TextView
    private lateinit var toolbarClearHistoryButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialization(container)

        toolbarClearHistoryButton.setOnClickListener {
            clearHistory()
        }


        return binding.root
    }

    private fun clearHistory() {

        val dialog = OptionsDialogBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text = requireContext().resources.getString(R.string.ClearHistory)
            optionsContent.text = requireContext().resources.getString(R.string.ClearHistoryDescription)
            positiveOption.text = requireContext().resources.getString(R.string.ClearHistory)
            positiveOption.setTextColor(ContextCompat.getColor(requireContext(), R.color.negative_red))
            negativeOption.text = requireContext().resources.getString(R.string.DontClearHistory)
            negativeOption.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color))
            positiveOption.setOnClickListener {
                bottomSheet.dismiss()
                Util.log("positive")
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
                Util.log("negative")
            }
        }
        dialog.root.setBottomSheet(bottomSheet)

    }

    private fun initialization(container: ViewGroup?) {

        _binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        toolbarText = binding.root.findViewById(R.id.toolbar_title)
        toolbarText.text = requireContext().resources.getString(R.string.History)
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
