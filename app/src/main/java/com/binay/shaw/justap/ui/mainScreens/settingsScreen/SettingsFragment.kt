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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.SettingsItemAdapter
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.databinding.ColorpickerModalBinding
import com.binay.shaw.justap.databinding.FragmentSettingsBinding
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.createBottomSheet
import com.binay.shaw.justap.helper.Util.dpToPx
import com.binay.shaw.justap.helper.Util.setBottomSheet
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.model.SettingsItem
import com.binay.shaw.justap.ui.authentication.signInScreen.SignInScreen
import com.binay.shaw.justap.mainViewModels.AccountsViewModel
import com.binay.shaw.justap.mainViewModels.LocalUserViewModel
import com.binay.shaw.justap.ui.mainScreens.qrScreens.qrGeneratorScreen.QRGeneratorViewModel
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
    private lateinit var qrGeneratorViewModel: QRGeneratorViewModel
    private lateinit var displayMetrics: DisplayMetrics
    private var overlay: Bitmap? = null
    private lateinit var sharedPref: SharedPreferences
    private var isUserVerified: Boolean = false

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        initialization()

        /**set List*/
        settingsItemList = ArrayList()

        settingsItemList.apply {
            add(SettingsItem(R.drawable.edit_icon, "Edit profile"))
            add(SettingsItem(R.drawable.scanner_icon, "Customize QR"))
            add(SettingsItem(R.drawable.info_icon, "About us"))
            add(SettingsItem(R.drawable.help_icon, "Need help?"))
            if (isUserVerified.not()) {
                add(SettingsItem(R.drawable.verify_icon, "Get verified"))
            }
            add(SettingsItem(R.drawable.logout_icon, "Log out"))
        }
        /**set find Id*/
        recyclerView = binding.settingsRV
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
                4 -> {
                    if (isUserVerified) {
                        logout()
                    } else {
                        offerVerification()
                    }
                }
                5 -> {
                    logout()
                }
            }
        }

        /**setRecycler view Adapter*/
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = settingsItemAdapter


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

    private fun customizeQR() {

        val dialog = ColorpickerModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            var isColorReset = false

            var firstSelectedColor = sharedPref.getInt(
                "firstColor",
                ResourcesCompat.getColor(resources, R.color.text_color, null)
            )
            var secondSelectedColor = sharedPref.getInt(
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
                        .setText(e.toString()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                            .substring(36))
                        .setBackgroundColorInt(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.negative_red
                            )
                        )
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
                        .setText(e.toString()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                            .substring(36))
                        .setBackgroundColorInt(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.negative_red
                            )
                        )
                        .setIcon(R.drawable.warning)
                        .setDuration(2500L)
                        .show()
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

                            saveColors(sharedPref, firstSelectedColor, secondSelectedColor)

                            Alerter.create(requireActivity())
                                .setTitle(resources.getString(R.string.changesSaved))
                                .setText(resources.getString(R.string.changesSavedDescription))
                                .setBackgroundColorInt(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.positive_green
                                    )
                                )
                                .setIcon(R.drawable.check)
                                .setDuration(2500L)
                                .show()
                            bottomSheet.dismiss()
                        } else {
                            Util.log("First Colors are : $firstSelectedColor and $defaultPrimaryColor")
                            Util.log("Second Colors are : $secondSelectedColor and $defaultSecondaryColor")
                            Toast.makeText(requireContext(), "No changes made", Toast.LENGTH_SHORT)
                                .show()
                        }

                    } else {
                        Util.log("Contrast choice is bad")
                        Alerter.create(requireActivity())
                            .setTitle(resources.getString(R.string.badContrast))
                            .setText(resources.getString(R.string.badContrastDescription))
                            .setBackgroundColorInt(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.negative_red
                                )
                            )
                            .setIcon(R.drawable.warning)
                            .setDuration(2000L)
                            .show()
                    }
                } catch (e: java.lang.IllegalArgumentException) {
                    Util.log("Exception: $e")
                    Alerter.create(requireActivity())
                        .setTitle(resources.getString(R.string.anErrorOccurred))
                        .setText(e.toString()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                            .substring(36))
                        .setBackgroundColorInt(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.negative_red
                            )
                        )
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

    private fun saveColors(
        sharedPreference: SharedPreferences,
        firstSelectedColor: Int,
        secondSelectedColor: Int
    ) {
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
            color2,
            isUserVerified
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
            ViewModelProvider(this@SettingsFragment)[QRGeneratorViewModel::class.java]

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
                    it.userProfilePicture,
                    it.userBannerPicture
                )
            }
            binding.settingsUserName.text = localUser.userName
            val profileURL = localUser.userProfilePicture.toString()
            if (profileURL.isNotEmpty())
                Util.loadImagesWithGlide(binding.profileImage, profileURL)
        }

        sharedPref = requireContext().getSharedPreferences("QRPref", Context.MODE_PRIVATE)
        isUserVerified = sharedPref.getBoolean("isVerified", false)
        binding.include.leftIcon.apply {
            feedback = this
            setImageResource(R.drawable.feedback_icon)
            visibility = View.VISIBLE
        }
    }

    private fun offerVerification() {

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text = "Verify your account"
            optionsContent.text =
                "You'll earn a verified badge from us ♥"
            positiveOption.text = "Request Verification"
            positiveOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.medium_blue
                )
            )

            negativeOption.text = "Cancel"
            negativeOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_color
                )
            )

            lottieAnimationLayout.apply {
                visibility = View.VISIBLE
                setAnimation(R.raw.verification_lottie)
            }

            positiveOption.setOnClickListener {
                bottomSheet.dismiss()
                //Send verification mail
                requestMailVerification()
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
                Util.log("Verification cancelled")
            }
        }
        dialog.root.setBottomSheet(bottomSheet)
    }

    private fun requestMailVerification() {
        val user = FirebaseAuth.getInstance().currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Email sent successfully, show a message to the user
                    Toast.makeText(requireContext(), "Verification email sent", Toast.LENGTH_SHORT)
                        .show()
                    val editor = sharedPref.edit()
                    editor.putBoolean("isVerified", true)
                    editor.apply()
                } else {
                    // Failed to send email verification, show an error message to the user
                    Toast.makeText(
                        requireContext(),
                        "Failed to send verification email",
                        Toast.LENGTH_SHORT
                    ).show()
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
                    sharedPref.edit().apply {
                        clear()
                        apply()
                    }
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