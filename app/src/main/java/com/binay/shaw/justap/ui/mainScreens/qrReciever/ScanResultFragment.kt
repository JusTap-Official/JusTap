package com.binay.shaw.justap.ui.mainScreens.qrReciever

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.ResultItemAdapter
import com.binay.shaw.justap.databinding.FragmentScanResultBinding
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.Companion.createBottomSheet
import com.binay.shaw.justap.helper.Util.Companion.setBottomSheet
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.User
import com.binay.shaw.justap.viewModel.ScanResultViewModel

class ResultFragment : Fragment() {

    private val args: ResultFragmentArgs by navArgs()
    private var _binding: FragmentScanResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbarTitle: TextView
    private lateinit var toolBarButton: ImageView
    private var showCaseAccountsList = mutableListOf<Accounts>()
    private val RESUME_URL: String =
        "https://binayshaw7777.github.io/BinayShaw.github.io/Binay%20Shaw%20CSE%2024.pdf"
    private lateinit var viewModel: ScanResultViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: ResultItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initialization(container)

        toolBarButton.setOnClickListener {
            handleBackButtonPress()
        }

        val data = args.resultString
        Util.log("Scanned Result: $data")

        if (args.isResult && !data.isNullOrEmpty()) {
            setUpResultView(args.resultString!!)
        } else {
            setUpAboutMe()
        }


        return binding.root
    }

    private fun setUpAboutMe() {
        binding.apply {
            profileImage.setImageResource(R.drawable.aboutme_pfp)
            profileBannerIV.setImageResource(R.drawable.aboutme_banner)
            profileNameTV.text = "Binay Shaw"
            profileBioTV.text = "Android Intern @HumaraNagar | Ex. @Edvora"
            downloadResume.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    val download = Intent(Intent.ACTION_VIEW, Uri.parse(RESUME_URL))
                    startActivity(download)
                }
            }
        }
        viewModel.getDevelopersAccount()
        viewModel.showCaseAccountsList.observe(viewLifecycleOwner) {
            binding.progressAnimation.progressParent.visibility = View.GONE
            showCaseAccountsList.addAll(it)
            recyclerViewAdapter.setData(it)
            recyclerViewAdapter.notifyDataSetChanged()
        }
    }


    private fun setUpResultView(resultString: String) {


        viewModel.getDataFromUserID(resultString)

        viewModel.showCaseAccountsList.observe(viewLifecycleOwner) {
            recyclerViewAdapter
            showCaseAccountsList.addAll(it)
            Util.log("ACCCCCC: $showCaseAccountsList")
            recyclerViewAdapter.setData(it)
            recyclerViewAdapter.notifyDataSetChanged()
        }

        viewModel.scanResultUser.observe(viewLifecycleOwner) {
            binding.progressAnimation.progressParent.visibility = View.GONE
            val tempUser = it
            recyclerViewAdapter.setUserData(it.name, it.email)

            binding.apply {
                profileNameTV.text = tempUser.name
                if (!tempUser.bio.isNullOrEmpty())
                    profileBioTV.text = tempUser.bio
                else
                    profileBioTV.visibility = View.GONE

                if (!tempUser.profilePictureURI.isNullOrEmpty()) {
                    Util.loadImagesWithGlide(binding.profileImage, tempUser.profilePictureURI)
                }
                if (!tempUser.profileBannerURI.isNullOrEmpty()) {
                    Util.loadImagesWithGlide(binding.profileBannerIV, tempUser.profileBannerURI)
                }
            }
        }

    }

    private fun initialization(container: ViewGroup?) {
        _binding = FragmentScanResultBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        toolbarTitle = binding.root.findViewById(R.id.toolbar_title)
        toolBarButton = binding.root.findViewById(R.id.leftIcon)
        toolBarButton.visibility = View.VISIBLE
        viewModel = ViewModelProvider(this@ResultFragment)[ScanResultViewModel::class.java]
        recyclerViewAdapter = ResultItemAdapter(requireContext())
        recyclerView = binding.accountsRv
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.progressAnimation.progressParent.visibility = View.VISIBLE
        binding.progressAnimation.progressText.text = "Preparing Result"

        if (args.isResult) {
            toolbarTitle.text = "Scan completed"
        } else {
            toolbarTitle.text = "About me"
        }

    }

    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                handleBackButtonPress()
                true
            } else false
        }
    }

    private fun handleBackButtonPress() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

}