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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.HistoryAdapter
import com.binay.shaw.justap.databinding.FragmentHistoryBinding
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.Companion.createBottomSheet
import com.binay.shaw.justap.helper.Util.Companion.setBottomSheet
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.LocalHistory
import com.binay.shaw.justap.viewModel.LocalHistoryViewModel


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var localUserHistoryViewModel: LocalHistoryViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private var accountsList = mutableListOf<LocalHistory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        initialization()

        historyAdapter.notifyDataSetChanged()

        binding.include.rightIcon.setOnClickListener {
            clearHistory()
        }

        binding.include.leftIcon.setOnClickListener {
            //Show Info Bottom Sheet
        }

        return binding.root
    }

    private fun clearHistory() {

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text = requireContext().resources.getString(R.string.ClearHistory)
            optionsContent.text =
                requireContext().resources.getString(R.string.ClearHistoryDescription)
            positiveOption.text = requireContext().resources.getString(R.string.ClearHistory)
            positiveOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.negative_red
                )
            )
            negativeOption.text = requireContext().resources.getString(R.string.DontClearHistory)
            negativeOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_color
                )
            )
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

    private fun initialization() {

        (activity as MainActivity).supportActionBar?.hide()
        binding.apply {
            include.apply {
                toolbarTitle.text = requireContext().resources.getString(R.string.History)
                rightIcon.visibility = View.VISIBLE
                leftIcon.visibility = View.VISIBLE
                leftIcon.setImageResource(R.drawable.info_icon)
            }
            recyclerView = historyRv
            historyAdapter = HistoryAdapter(requireContext()) { historyUser ->
                //Handle on click
            }

            recyclerView = binding.historyRv
            recyclerView.apply {
                adapter = historyAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

        }

        localUserHistoryViewModel = ViewModelProvider(
            this@HistoryFragment,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[LocalHistoryViewModel::class.java]

        localUserHistoryViewModel.getAllHistory.observe(viewLifecycleOwner) {
            Util.log("Accounts Scanned: $it")
            accountsList.clear()
            accountsList.addAll(it)
            historyAdapter.setData(it)
            historyAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
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
