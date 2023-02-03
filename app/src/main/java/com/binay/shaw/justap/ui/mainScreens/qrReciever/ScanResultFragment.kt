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
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.navArgs
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentScanResultBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.ui.mainScreens.accountFragments.AddEditFragmentArgs

class ResultFragment : Fragment() {

    private val args: ResultFragmentArgs by navArgs()
    private var _binding: FragmentScanResultBinding ?= null
    private val binding get() = _binding!!
    private lateinit var toolbarTitle: TextView
    private lateinit var toolBarButton: ImageView
    private var showCaseAccountsList = mutableListOf<Accounts>()
    private val RESUME_URL: String = "https://binayshaw7777.github.io/BinayShaw.github.io/Binay%20Shaw%20CSE%2024.pdf"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initialization(container)

        if (args.isResult) {
            setUpResultView()
        } else {
            setUpAboutMe()
        }

        val userID = args.resultString
        Util.log("UserID: $userID")





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
        showCaseAccountsList = getDeveloperAccounts()
    }

    private fun getDeveloperAccounts(): MutableList<Accounts> {
        val accounts = mutableListOf<Accounts>()

        accounts.add(Accounts(
            1,
            "Email",
            "binayshaw7777@gmail.com",
            true
        ))
        accounts.add(Accounts(
            3,
            "LinkedIn",
            "https://www.linkedin.com/in/binayshaw7777/",
            true
        ))
        accounts.add(
            Accounts(
            5,
            "Twitter",
                "https://twitter.com/binayplays7777",
                true
        ))
        accounts.add(
            Accounts(
            9,
            "Website",
                "https://binayshaw7777.github.io/BinayShaw.github.io/",
                true
        ))

        return accounts
    }

    private fun setUpResultView() {

    }

    private fun initialization(container: ViewGroup?) {
        _binding = FragmentScanResultBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        toolbarTitle = binding.root.findViewById(R.id.toolbar_title)
        toolBarButton = binding.root.findViewById(R.id.leftIcon)
        toolBarButton.visibility = View.VISIBLE

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
                requireActivity().onBackPressedDispatcher.onBackPressed()
                true
            } else false
        }
    }

}