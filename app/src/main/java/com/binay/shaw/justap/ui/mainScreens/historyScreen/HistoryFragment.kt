package com.binay.shaw.justap.ui.mainScreens.historyScreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.HistoryAdapter
import com.binay.shaw.justap.databinding.FragmentHistoryBinding
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.createBottomSheet
import com.binay.shaw.justap.helper.Util.setBottomSheet
import com.binay.shaw.justap.model.LocalHistory
import com.tapadoo.alerter.Alerter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

        clickHandlers()

        return binding.root
    }

    private fun clickHandlers() {
        setFilter()
        historyIconHandler()
        infoHandler()
    }

    private fun infoHandler() {
        binding.include.leftIcon.setOnClickListener {
            //Show Info Bottom Sheet
        }
    }

    private fun historyIconHandler() {
        binding.include.rightIcon.setOnClickListener {
            if (historyAdapter.itemCount > 0)
                clearHistory()
            else
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.no_data_to_clear),
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setFilter() {

        binding.etFilter.apply {

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    if (p0.toString().isNotEmpty()) {
                        filter(Util.getBaseStringForFiltering(p0.toString().lowercase()))
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.clear_text, 0)
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, 0, 0)
                        historyAdapter.setData(accountsList)
                    }
                }

            })

            setOnTouchListener(View.OnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                        this.setText("")
                        return@OnTouchListener true
                    }
                }
                return@OnTouchListener false
            })
        }
    }

    private fun filter(search: String) {
        val filterList = mutableListOf<LocalHistory>()
        if (search.isNotEmpty()) {
            for (current in accountsList) {
                if (Util.getBaseStringForFiltering(current.username.lowercase()).contains(search)) {
                    filterList.add(current)
                }
            }
            historyAdapter.setData(filterList)
        } else
            historyAdapter.setData(accountsList)
    }

    @SuppressLint("NotifyDataSetChanged")
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
                lifecycleScope.launch(Dispatchers.IO) {
                    localUserHistoryViewModel.clearHistory()
                    withContext(Dispatchers.Main) {
                        historyAdapter.notifyDataSetChanged()
                        Alerter.create(requireActivity())
                            .setTitle(resources.getString(R.string.clearHistory))
                            .setText(resources.getString(R.string.clearHistoryDescription))
                            .setBackgroundColorInt(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.positive_green
                                )
                            )
                            .setIcon(R.drawable.delete)
                            .setDuration(2000L)
                            .show()
                    }
                }
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
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
                if (Util.checkForInternet(requireContext())) {
                    val action = HistoryFragmentDirections.actionHistoryToResultFragment(
                        historyUser.userID,
                        true
                    )
                    findNavController().navigate(action)
                } else {
                    Util.showNoInternet(requireActivity())
                }
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
