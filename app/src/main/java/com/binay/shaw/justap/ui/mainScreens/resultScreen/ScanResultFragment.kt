package com.binay.shaw.justap.ui.mainScreens.resultScreen

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.ResultItemAdapter
import com.binay.shaw.justap.databinding.FragmentScanResultBinding
import com.binay.shaw.justap.databinding.MyToolbarBinding
import com.binay.shaw.justap.helper.LinksUtils
import com.binay.shaw.justap.helper.NotificationHelper
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.User
import com.binay.shaw.justap.ui.mainScreens.historyScreen.LocalHistoryViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


class ResultFragment : Fragment() {

    private val args: ResultFragmentArgs by navArgs()
    private var _binding: FragmentScanResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolBar: MyToolbarBinding
    private var showCaseAccountsList = mutableListOf<Accounts>()
    private lateinit var viewModel: ScanResultViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: ResultItemAdapter

    private lateinit var notificationHelper: NotificationHelper
    private val channelId = "NotificationChannelId"
    private val channelName = "Notification Channel Name"
    private val notificationId = 1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScanResultBinding.inflate(layoutInflater, container, false)
        initialization()

        toolBar.leftIcon.setOnClickListener {
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
            profileNameTV.text = getString(R.string.BinayShaw)
            profileBioTV.text = getString(R.string.AboutMeDescription)
            downloadResume.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    val download = Intent(Intent.ACTION_VIEW, Uri.parse(Util.resumeURL))
                    startActivity(download)
                }
            }
        }
        viewModel.getDevelopersAccount()
        viewModel.showCaseAccountsListDevLiveData.observe(viewLifecycleOwner) {
            binding.progressAnimation.progressParent.visibility = View.GONE
            showCaseAccountsList.clear()
            recyclerViewAdapter.clearData()
            showCaseAccountsList.addAll(it)
            recyclerViewAdapter.setData(it)
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpResultView(resultString: String) {

        val localUserHistoryViewModel = ViewModelProvider(
            this@ResultFragment,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[LocalHistoryViewModel::class.java]

        viewModel.getDataFromUserID(resultString)

        viewModel.showCaseAccountsListLiveData.observe(viewLifecycleOwner) {
            showCaseAccountsList.clear()
            recyclerViewAdapter.clearData()
            for (accounts in it) {
                if (accounts.showAccount) {
                    showCaseAccountsList.add(accounts)
                }
            }
            recyclerViewAdapter.setData(showCaseAccountsList)
        }

        viewModel.scanResultUser.observe(viewLifecycleOwner) {

            binding.progressAnimation.progressParent.visibility = View.GONE

            createLocalHistory(it, localUserHistoryViewModel)

            val tempUser = it

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

                val builder = createNotificationBuilder(it.name)
                notificationHelper.showNotification(notificationId, builder)

            }
        }

    }

    private fun createLocalHistory(user: User, localHistoryViewModel: LocalHistoryViewModel) {

        if (user.profilePictureURI != null) {
            if (user.profilePictureURI.isNotEmpty()) {
                val myOptions = RequestOptions()
                    .override(100, 100)
                Glide.with(requireContext())
                    .asBitmap()
                    .apply(myOptions)
                    .encodeFormat(Bitmap.CompressFormat.PNG) // Set the output format to PNG
                    .encodeQuality(50)
                    .load(user.profilePictureURI)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            // Save the bitmap to your LocalHistory object
                            viewModel.saveLocalHistory(user, resource, localHistoryViewModel)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // Do nothing
                        }
                    })
            }else {
                viewModel.saveLocalHistory(user, null, localHistoryViewModel)
            }
        } else {
            viewModel.saveLocalHistory(user, null, localHistoryViewModel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createNotificationBuilder(name: String): NotificationCompat.Builder {

        // Create an Intent for the notification action
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Create a NotificationCompat.Builder object
        return NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(resources.getString(R.string.successfullyScanned))
            .setContentText("${Util.getFirstName(name)} was added in history")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
    }

    private fun initialization() {
        (activity as MainActivity).supportActionBar?.hide()

        toolBar = binding.include
        toolBar.leftIcon.visibility = View.VISIBLE
        viewModel = ViewModelProvider(this@ResultFragment)[ScanResultViewModel::class.java]
        recyclerViewAdapter = ResultItemAdapter {
            LinksUtils.processData(it, requireContext())
        }
        recyclerView = binding.accountsRv
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerViewAdapter
        }
        binding.progressAnimation.apply {
            progressParent.visibility = View.VISIBLE
            progressText.text = resources.getString(R.string.PreparingResult)
        }

        // Create the Notification Channel
        notificationHelper = NotificationHelper(requireContext())
        notificationHelper.createNotificationChannel(channelId, channelName)

        if (args.isResult) {
            toolBar.toolbarTitle.text = resources.getString(R.string.ScanCompleted)
        } else {
            toolBar.toolbarTitle.text = resources.getString(R.string.AboutMe)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun handleBackButtonPress() =
        requireActivity().onBackPressedDispatcher.onBackPressed()
}