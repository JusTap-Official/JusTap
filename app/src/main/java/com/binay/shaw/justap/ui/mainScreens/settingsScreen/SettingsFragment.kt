package com.binay.shaw.justap.ui.mainScreens.settingsScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.SettingsItemAdapter
import com.binay.shaw.justap.base.BaseFragment
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.databinding.ColorpickerModalBinding
import com.binay.shaw.justap.databinding.FragmentSettingsBinding
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.databinding.ParagraphModalBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.createBottomSheet
import com.binay.shaw.justap.helper.Util.dpToPx
import com.binay.shaw.justap.helper.Util.setBottomSheet
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.model.SettingsItem
import com.binay.shaw.justap.ui.authentication.signInScreen.SignInScreen
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.binay.shaw.justap.ui.mainScreens.qrScreens.qrGeneratorScreen.QRGeneratorViewModel
import com.google.firebase.auth.FirebaseAuth
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


class SettingsFragment : BaseFragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsItemList: ArrayList<SettingsItem>
    private lateinit var settingsItemAdapter: SettingsItemAdapter
    private lateinit var localUserDatabase: LocalUserDatabase
    private val localUserViewModel by viewModels<LocalUserViewModel> { ViewModelFactory() }
    private lateinit var feedback: ImageView
    private lateinit var localUser: LocalUser
    private val qrGeneratorViewModel by viewModels<QRGeneratorViewModel> { ViewModelFactory() }
    private lateinit var displayMetrics: DisplayMetrics
    private var overlay: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        initObservers()
        initialization()
        handleOperations()

        binding.toProfile.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_settings_to_profileFragment)
        }

        feedback.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(requireContext().resources.getString(R.string.mailTo))
            startActivity(openURL)
        }

        return binding.root
    }

    private fun handleOperations() {
        setupSettingsOptions()
    }

    private fun setupSettingsOptions() {
        /**set List*/
        settingsItemList = ArrayList()
        settingsItemList.apply {
            add(SettingsItem(1, R.drawable.edit_icon, "Edit profile", false))
            add(SettingsItem(3, R.drawable.scanner_icon, "Customize QR", false))
            add(SettingsItem(4, R.drawable.info_icon, "About us", false))
            add(SettingsItem(5, R.drawable.help_icon, "Need help?", false))
            add(SettingsItem(6, R.drawable.logout_icon, "Log out", false))
        }

        /**set find Id*/
        /**set Adapter*/
        settingsItemAdapter = SettingsItemAdapter(requireContext(), settingsItemList) {
            //Customize QR Listener
            when (it) {
                0 -> {
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_settings_to_editProfileFragment)
                }

                1 -> customizeQR()

                2 -> {
                    val action = SettingsFragmentDirections.actionSettingsToResultFragment(
                        resultString = null,
                        isResult = false
                    )
                    Navigation.findNavController(binding.root).navigate(action)
                }
                3 -> {
                    needHelp()
                }
                4 -> {
                    logout()
                }
            }
        }
        /**setRecycler view Adapter*/
        binding.settingsRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = settingsItemAdapter
        }
    }

    private fun initObservers() {
        localUserViewModel.fetchUser.observe(viewLifecycleOwner) {
            if (it != null) {
                localUser = LocalUser(
                    it.userID,
                    it.userName,
                    it.userEmail,
                    it.userBio,
                    it.userProfilePicture,
                    it.userBannerPicture
                )
            }
            val name = Util.getFirstName(localUser.userName)
            binding.settingsUserName.text = name

            val profileURL = localUser.userProfilePicture.toString()
            if (profileURL.isNotEmpty())
                Util.loadImagesWithGlide(binding.profileImage, profileURL)
        }
    }

    private fun needHelp() {
        val dialog = ParagraphModalBinding.inflate(layoutInflater)
        val bottomSheet = requireActivity().createBottomSheet()
        dialog.apply {
            paragraphHeading.text = resources.getString(R.string.welcome_to_justap)
            paragraphContent.text = resources.getString(R.string.needHelpSettingsDescription)
            lottieAnimationLayout.apply {
                setAnimation(R.raw.help_lottie)
                visibility = View.VISIBLE
            }
        }
        dialog.root.setBottomSheet(bottomSheet)
    }

    private fun customizeQR() {

        val sharedPreference = requireContext().getSharedPreferences("QRPref", Context.MODE_PRIVATE)

        val dialog = ColorpickerModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            var isColorReset = false

            var firstSelectedColor = sharedPreference.getInt(
                "firstColor",
                ResourcesCompat.getColor(resources, R.color.text_color, null)
            )
            var secondSelectedColor = sharedPreference.getInt(
                "secondColor",
                ResourcesCompat.getColor(resources, R.color.bg_color, null)
            )

            val defaultPrimaryColor = ResourcesCompat.getColor(
                resources,
                R.color.text_color,
                null
            )
            val defaultSecondaryColor = ResourcesCompat.getColor(
                resources,
                R.color.bg_color,
                null
            )

            generateQR(
                firstSelectedColor,
                secondSelectedColor
            )

            qrGeneratorViewModel.bitmap.observe(viewLifecycleOwner) {
                qrCodePreview.setImageBitmap(it)
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
                    showAlerter(
                        resources.getString(R.string.anErrorOccurred),
                        e.toString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                        .substring(36),
                        ContextCompat.getColor(requireContext(), R.color.negative_red),
                        R.drawable.warning,
                        2000L
                    )
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
                    showAlerter(
                        resources.getString(R.string.anErrorOccurred),
                        e.toString()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                            .substring(36),
                        ContextCompat.getColor(requireContext(), R.color.negative_red),
                        R.drawable.warning,
                        2000L
                    )
                }
            }

            resetColors.setOnClickListener {
                generateQR(
                    defaultPrimaryColor,
                    defaultSecondaryColor
                )
                firstSelectedColor = defaultPrimaryColor
                secondSelectedColor = defaultSecondaryColor
                isColorReset = true
            }

            positiveOption.setOnClickListener {
                try {

                    val contrastRatio = 1.5f
                    val contrastVar1 =
                        ColorUtils.calculateContrast(firstSelectedColor, secondSelectedColor)
                    val contrastVar2 =
                        ColorUtils.calculateContrast(firstSelectedColor, secondSelectedColor)
                    Util.log("Contrast is: $contrastVar1 and $contrastVar2")

                    if (contrastVar1 > contrastRatio || contrastVar2 > contrastRatio) {
                        Util.log("Contrast Choice is Okay")


                        if (Util.colorIsNotTheSame(firstSelectedColor, defaultPrimaryColor)
                            || Util.colorIsNotTheSame(secondSelectedColor, defaultSecondaryColor)
                            || isColorReset
                        ) {

                            saveColors(sharedPreference, firstSelectedColor, secondSelectedColor)
                            showAlerter(
                                resources.getString(R.string.changesSaved),
                                resources.getString(R.string.changesSavedDescription),
                                ContextCompat.getColor(requireContext(), R.color.positive_green),
                                R.drawable.check,
                                2500L
                            )
                            bottomSheet.dismiss()
                        } else {
                            Util.log("First Colors are : $firstSelectedColor and $defaultPrimaryColor")
                            Util.log("Second Colors are : $secondSelectedColor and $defaultSecondaryColor")
                            Toast.makeText(requireContext(), "No changes made", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        showAlerter(
                            resources.getString(R.string.badContrast),
                            resources.getString(R.string.badContrastDescription),
                            ContextCompat.getColor(requireContext(), R.color.negative_red),
                            R.drawable.warning,
                            2000L
                        )
                        Util.log("Contrast choice is bad")
                    }
                } catch (e: java.lang.IllegalArgumentException) {
                    Util.log("Exception: $e")
                    showAlerter(
                        resources.getString(R.string.anErrorOccurred),
                        e.toString()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                            .substring(36),
                        ContextCompat.getColor(requireContext(), R.color.negative_red),
                        R.drawable.warning,
                        2500L
                    )
                }
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
                Util.log("Customization of QR is Cancelled.")
            }
        }
        dialog.root.setBottomSheet(bottomSheet)
    }

    private fun saveColors(sharedPreference: SharedPreferences, firstSelectedColor: Int, secondSelectedColor: Int) {
        val editor = sharedPreference.edit()
        editor.putInt("firstColor", firstSelectedColor)
        editor.putInt("secondColor", secondSelectedColor)
        editor.apply()
        Util.log("Saved")
    }

    private fun generateQR(color1: Int, color2: Int) {
        qrGeneratorViewModel.generateQR(
            displayMetrics, overlay,
            color1,
            color2
        )
    }


    @SuppressLint("SetTextI18n")
    private fun initialization() {

        (activity as MainActivity).supportActionBar?.hide()
        binding.include.toolbarTitle.text =
            requireContext().resources.getString(R.string.Settings)
        localUserDatabase = Room.databaseBuilder(
            requireContext(), LocalUserDatabase::class.java,
            "localDB"
        ).build()

        displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        overlay = ContextCompat.getDrawable(requireContext(), R.drawable.logo_black_stroke)
            ?.toBitmap(72.dpToPx(), 72.dpToPx())

        binding.include.apply {
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
                        val intent = Intent(requireContext(), SignInScreen::class.java)
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