package com.binay.shaw.justap.ui.mainScreens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.SettingsItemAdapter
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.databinding.ColorpickerModalBinding
import com.binay.shaw.justap.databinding.FragmentSettingsBinding
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.Companion.createBottomSheet
import com.binay.shaw.justap.helper.Util.Companion.dpToPx
import com.binay.shaw.justap.helper.Util.Companion.setBottomSheet
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.model.SettingsItem
import com.binay.shaw.justap.ui.authentication.SignIn_Screen
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.binay.shaw.justap.viewModel.QRGenerator_ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.tapadoo.alerter.Alerter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var settingsItemList: ArrayList<SettingsItem>
    private lateinit var settingsItemAdapter: SettingsItemAdapter
    private lateinit var localUserDatabase: LocalUserDatabase
    private lateinit var localUserViewModel: LocalUserViewModel
    private lateinit var accountsViewModel: AccountsViewModel
    private lateinit var logoutIV: ImageView
    private lateinit var feedback: ImageView
    private lateinit var localUser: LocalUser
    private lateinit var qrGeneratorViewModel: QRGenerator_ViewModel
    private lateinit var displayMetrics: DisplayMetrics
    private var overlay: Bitmap? = null

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        initialization()

        /**set List*/
        settingsItemList = ArrayList()

        settingsItemList.add(SettingsItem(1, R.drawable.edit_icon, "Edit profile", false))
        settingsItemList.add(SettingsItem(3, R.drawable.scanner_icon, "Customize QR", false))
        settingsItemList.add(SettingsItem(4, R.drawable.info_icon, "About us", false))
        settingsItemList.add(SettingsItem(5, R.drawable.help_icon, "Need help?", false))
        /**set find Id*/
        recyclerView = binding.settingsRV
        /**set Adapter*/
        settingsItemAdapter = SettingsItemAdapter(requireContext(), settingsItemList) {
            //Customize QR Listener
            if (it == 1) {
                customizeQR()
            }
        }

        /**setRecycler view Adapter*/
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = settingsItemAdapter


        binding.toProfile.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_settings_to_profileFragment)
        }

        binding.include.rightIcon.setOnClickListener {
            logout()
        }

        feedback.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(requireContext().resources.getString(R.string.mailTo))
            startActivity(openURL)
        }

        return binding.root
    }

    private fun customizeQR() {

        val sharedPreference = requireContext().getSharedPreferences("QRPref", Context.MODE_PRIVATE)

        val dialog = ColorpickerModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            var firstSelectedColor = sharedPreference.getInt(
                "firstColor",
                ResourcesCompat.getColor(resources, R.color.text_color, null)
            )
            var secondSelectedColor = sharedPreference.getInt(
                "secondColor",
                ResourcesCompat.getColor(resources, R.color.bg_color, null)
            )

            generateQR(
                firstSelectedColor,
                secondSelectedColor
            )

            qrGeneratorViewModel.status.observe(viewLifecycleOwner) {
                qrCodePreview.setImageBitmap(qrGeneratorViewModel.bitmap.value)
            }

            optionsHeading.text = resources.getString(R.string.customizeQR)
            optionsContent.text = resources.getString(R.string.customizeQRDescription)
            positiveOption.text = resources.getString(R.string.SaveChanges)
            negativeOption.text = requireContext().resources.getString(R.string.DontSave)

            pickColorFor1.setOnClickListener {
                try {
                    val builder = ColorPickerDialog.Builder(requireContext())
                        .setTitle(resources.getString(R.string.chooseColorForForeground))
                        .setPositiveButton(
                            resources.getString(R.string.SaveChanges),
                            ColorEnvelopeListener { envelope, _ ->
                                firstSelectedColor = envelope.color
                                generateQR(
                                    firstSelectedColor,
                                    secondSelectedColor
                                )
                            }
                        )
                        .setNegativeButton(
                            resources.getString(R.string.cancel)
                        ) { dialogInterface, _ -> dialogInterface.dismiss() }
                    builder.colorPickerView.flagView =
                        BubbleFlag(requireContext()).apply { flagMode = FlagMode.FADE }
                    builder.show()
                } catch (e: java.lang.IllegalArgumentException) {
                    Util.log("Exception: $e")
                    Alerter.create(requireActivity())
                        .setTitle(resources.getString(R.string.anErrorOccurred))
                        .setText(e.toString().capitalize(Locale.ROOT).substring(36))
                        .setBackgroundColorInt(resources.getColor(R.color.negative_red))
                        .setIcon(R.drawable.warning)
                        .setDuration(2500L)
                        .show()
                }
            }

            pickColorFor2.setOnClickListener {
                try {

                    val builder = ColorPickerDialog.Builder(requireContext())
                        .setTitle(resources.getString(R.string.chooseColorForBackground))
                        .setPositiveButton(
                            resources.getString(R.string.SaveChanges),
                            ColorEnvelopeListener { envelope, _ ->
                                secondSelectedColor = envelope.color
                                generateQR(
                                    firstSelectedColor,
                                    secondSelectedColor
                                )
                            }
                        )
                        .setNegativeButton(
                            resources.getString(R.string.cancel)
                        ) { dialogInterface, _ ->
                            dialogInterface.dismiss()
                        }
                    builder.colorPickerView.flagView =
                        BubbleFlag(requireContext()).apply { flagMode = FlagMode.FADE }
                    builder.show()
                } catch (e: java.lang.IllegalArgumentException) {
                    Util.log("Exception: $e")
                    Alerter.create(requireActivity())
                        .setTitle(resources.getString(R.string.anErrorOccurred))
                        .setText(e.toString().capitalize(Locale.ROOT).substring(36))
                        .setBackgroundColorInt(resources.getColor(R.color.negative_red))
                        .setIcon(R.drawable.warning)
                        .setDuration(2500L)
                        .show()
                }
            }

            positiveOption.setOnClickListener {
                try {

                    val contrastRatio = 3.5f
                    val contrastVar1 =
                        ColorUtils.calculateContrast(firstSelectedColor, secondSelectedColor)
                    val contrastVar2 =
                        ColorUtils.calculateContrast(firstSelectedColor, secondSelectedColor)
                    Util.log("Contrast is: $contrastVar1 and $contrastVar2")

                    if (contrastVar1 > contrastRatio || contrastVar2 > contrastRatio) {

                        if (firstSelectedColor != ResourcesCompat.getColor(
                                resources,
                                R.color.text_color,
                                null
                            ) &&
                            secondSelectedColor != ResourcesCompat.getColor(
                                resources,
                                R.color.bg_color,
                                null
                            )
                        ) {

                            val editor = sharedPreference.edit()
                            editor.putInt("firstColor", firstSelectedColor)
                            editor.putInt("secondColor", secondSelectedColor)
                            editor.apply()
                            Util.log("Saved")

                            Alerter.create(requireActivity())
                                .setTitle(resources.getString(R.string.changesSaved))
                                .setText(resources.getString(R.string.changesSavedDescription))
                                .setBackgroundColorInt(resources.getColor(R.color.positive_green))
                                .setIcon(R.drawable.check)
                                .setDuration(2500L)
                                .show()
                        }
                        bottomSheet.dismiss()

                    } else {
                        Util.log("Contrast choice is bad")
                        Alerter.create(requireActivity())
                            .setTitle(resources.getString(R.string.badContrast))
                            .setText(resources.getString(R.string.badContrastDescription))
                            .setBackgroundColorInt(resources.getColor(R.color.negative_red))
                            .setIcon(R.drawable.warning)
                            .setDuration(2000L)
                            .show()
                    }
                } catch (e: java.lang.IllegalArgumentException) {
                    Util.log("Exception: $e")
                    Alerter.create(requireActivity())
                        .setTitle(resources.getString(R.string.anErrorOccurred))
                        .setText(e.toString().capitalize(Locale.ROOT).substring(36))
                        .setBackgroundColorInt(resources.getColor(R.color.negative_red))
                        .setIcon(R.drawable.warning)
                        .setDuration(2500L)
                        .show()
                }
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
                Util.log("Customization of QR is Cancelled.")
            }
        }
        dialog.root.setBottomSheet(bottomSheet)
    }

    private fun generateQR(color1: Int, color2: Int) {
        qrGeneratorViewModel.generateQR(
            displayMetrics, overlay,
            color1,
            color2
        )
    }


    private fun initialization() {

        (activity as MainActivity).supportActionBar?.hide()
        binding.include.toolbarTitle.text =
            requireContext().resources.getString(R.string.Settings)
        localUserDatabase = Room.databaseBuilder(
            requireContext(), LocalUserDatabase::class.java,
            "localDB"
        ).build()
        localUserViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[LocalUserViewModel::class.java]

        accountsViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[AccountsViewModel::class.java]

        qrGeneratorViewModel =
            ViewModelProvider(this@SettingsFragment)[QRGenerator_ViewModel::class.java]

        displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        overlay = ContextCompat.getDrawable(requireContext(), R.drawable.logo_black_stroke)
            ?.toBitmap(72.dpToPx(), 72.dpToPx())

        localUserViewModel.fetchUser.observe(viewLifecycleOwner) {
            if (it != null) {
                localUser = LocalUser(
                    it.userID,
                    it.userName,
                    it.userEmail,
                    it.userBio,
                    it.userPFPBase64,
                    it.userProfilePicture,
                    it.userBannerPicture
                )
            }
            binding.settingsUserName.text = localUser.userName
            val profileURL = localUser.userProfilePicture.toString()
            if (profileURL.isNotEmpty())
                Util.loadImagesWithGlide(binding.profileImage, profileURL)
        }

        binding.include.apply {
            rightIcon.apply {
                logoutIV = this
                setImageResource(R.drawable.logout_icon)
                visibility = View.VISIBLE
            }
            leftIcon.apply {
                feedback = this
                setImageResource(R.drawable.feedback_icon)
                visibility = View.VISIBLE
            }
        }
    }

    private fun logout() {

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text = requireContext().resources.getString(R.string.LogoutTitle)
            optionsContent.text =
                requireContext().resources.getString(R.string.LogoutDescription)
            positiveOption.text = requireContext().resources.getString(R.string.LogoutTitle)
            positiveOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.negative_red
                )
            )

            negativeOption.text = requireContext().resources.getString(R.string.LogoutCancel)
            negativeOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_color
                )
            )

            positiveOption.setOnClickListener {
                bottomSheet.dismiss()

                lifecycleScope.launch(Dispatchers.Main) {
                    val signOutFromFirebase =
                        launch(Dispatchers.IO) { FirebaseAuth.getInstance().signOut() }
                    signOutFromFirebase.join()
                    LocalUserDatabase.getDatabase(requireContext()).clearTables()
                    withContext(Dispatchers.Main) {
                        val intent = Intent(requireContext(), SignIn_Screen::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent).also { requireActivity().finish() }
                        Util.log("Logged out")

                    }
                }
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
                Util.log("Logout cancelled")
            }
        }
        dialog.root.setBottomSheet(bottomSheet)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}