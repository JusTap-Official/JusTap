package com.binay.shaw.justap.ui.mainScreens.accountFragments

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentAddEditBinding
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.databinding.ParagraphModalBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.Companion.createBottomSheet
import com.binay.shaw.justap.helper.Util.Companion.setBottomSheet
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.viewModel.AddEditViewModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class AddEditFragment : Fragment() {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbarText: TextView
    private lateinit var toolbarBackButton: ImageView
    private lateinit var toolbarDeleteIcon: ImageView
    private val args: AddEditFragmentArgs by navArgs()
    private var selectedAccount: String? = null
    private lateinit var viewModel: AddEditViewModel
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var accountsViewModel: AccountsViewModel


    private fun chooseAccount(it: String) {

        when (it) {
            "Phone" -> {
                binding.accountData.inputType = InputType.TYPE_CLASS_PHONE
                setImageOnAccountNameChange(R.drawable.phone)
            }
            "Email" -> {
                binding.accountData.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                setImageOnAccountNameChange(R.drawable.email)
            }
            "Instagram" -> setImageOnAccountNameChange(R.drawable.instagram)
            "LinkedIn" -> setImageOnAccountNameChange(R.drawable.linkedin)
            "Facebook" -> setImageOnAccountNameChange(R.drawable.facebook)
            "Twitter" -> setImageOnAccountNameChange(R.drawable.twitter)
            "YouTube" -> setImageOnAccountNameChange(R.drawable.youtube)
            "Snapchat" -> setImageOnAccountNameChange(R.drawable.snapchat)
            "Twitch" -> setImageOnAccountNameChange(R.drawable.twitch)
            "Website" -> setImageOnAccountNameChange(R.drawable.website)
            "Discord" -> setImageOnAccountNameChange(R.drawable.discord)
            "LinkTree" -> setImageOnAccountNameChange(R.drawable.linktree)
            "Custom Link" -> setImageOnAccountNameChange(R.drawable.custom_link)
            "Telegram" -> setImageOnAccountNameChange(R.drawable.telegram)
            "Spotify" -> setImageOnAccountNameChange(R.drawable.spotify)
            "WhatsApp" -> {
                binding.accountData.inputType = InputType.TYPE_CLASS_PHONE
                setImageOnAccountNameChange(R.drawable.whatsapp)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialization(container)

        binding.cancelChanges.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.info.setOnClickListener {
            val dialog = ParagraphModalBinding.inflate(layoutInflater)
            val bottomSheet = requireActivity().createBottomSheet()
            dialog.apply {
                paragraphHeading.text = "Enter URL or Username"
                paragraphContent.text =
                    "You must either enter your url or username of that account in order to proceed and save your data"
            }
            dialog.root.setBottomSheet(bottomSheet)
        }

        toolbarDeleteIcon.setOnClickListener {
            deleteAccount()
        }



        binding.accountName.afterTextChanged {
            selectedAccount = it
            chooseAccount(it)
        }


        binding.confirmChanges.setOnClickListener {
            val accountData = binding.accountData.text.toString()
            if (dataIsValid(selectedAccount, accountData)) {
                //Save data here
                saveData(accountData)
            } else {
                Toast.makeText(requireContext(), "Fill all input fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }

        return binding.root
    }

    private fun deleteAccount() {

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text = requireContext().resources.getString(R.string.ConfirmChanges)
            optionsContent.text =
                "Are you sure you want to delete this account type?"
            positiveOption.text = "Delete"
            positiveOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.negative_red
                )
            )
            negativeOption.text = "Don't delete"
            negativeOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_color
                )
            )
            positiveOption.setOnClickListener {
                bottomSheet.dismiss()
                if (Util.checkForInternet(requireContext())) {
                    binding.progressAnimation.progressParent.visibility = View.VISIBLE

                    args.accounts?.let {
                        val array = resources.getStringArray(R.array.account_names)
                        val index = array.indexOf(it.accountName)
                        it.accountID = index

                        lifecycleScope.launch {
                            viewModel.deleteEntry(accountsViewModel, firebaseDatabase, it)

                            viewModel.deleteStatus.observe(viewLifecycleOwner) { status ->
                                if (status == 3) {
                                    Util.log("Status value = $status")
                                    viewModel.deleteStatus.postValue(0)
                                    Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT)
                                        .show()
                                    binding.progressAnimation.progressParent.visibility = View.GONE
                                    findNavController().navigateUp()
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "No Internet!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
                Util.log("Don't Delete")
            }
            dialog.root.setBottomSheet(bottomSheet)
        }
    }


    private fun saveData(accountData: String) {

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text =
                requireContext().resources.getString(R.string.ConfirmChanges)
            optionsContent.text =
                requireContext().resources.getString(R.string.ConfirmChangesDescription)
            positiveOption.text = requireContext().resources.getString(R.string.SaveChanges)
            positiveOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.negative_red
                )
            )
            negativeOption.text = requireContext().resources.getString(R.string.DontSave)
            negativeOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_color
                )
            )
            positiveOption.setOnClickListener {
                bottomSheet.dismiss()
                if (Util.checkForInternet(requireContext())) {
                    binding.progressAnimation.progressParent.visibility = View.VISIBLE

                    //Saving Data
                    lifecycleScope.launch {
                        selectedAccount?.let { it1 ->
                            getStringIndex(it1)
                        }?.let { index ->
                            if (args.mode == 0) {
                                //Save new Data
                                viewModel.saveData(
                                    accountsViewModel,
                                    firebaseDatabase,
                                    Util.userID,
                                    index,
                                    selectedAccount!!,
                                    accountData
                                )
                                viewModel.saveStatus.observe(viewLifecycleOwner) {
                                    if (it == 3) {
                                        viewModel.saveStatus.value = 0
                                        //Success
                                        Toast.makeText(
                                            requireContext(),
                                            "Saved Data",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        binding.progressAnimation.progressParent.visibility =
                                            View.GONE
                                        requireActivity().onBackPressedDispatcher.onBackPressed()
                                    }
                                }
                            } else {
                                //Update current Data
                                viewModel.updateData(
                                    firebaseDatabase,
                                    Util.userID,
                                    index,
                                    selectedAccount!!,
                                    accountData
                                )
                            }
                        }

                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No Internet!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
                Util.log("Don't Save")
            }
            dialog.root.setBottomSheet(bottomSheet)
        }
    }

    private fun getStringIndex(string: String): Int {
        val stringArray = resources.getStringArray(R.array.account_names)
        return stringArray.indexOf(string)
    }

    private fun dataIsValid(selectedAccount: String?, accountData: String): Boolean {
        if (selectedAccount.isNullOrEmpty() ||
            accountData.isEmpty()
        )
            return false
        return true
    }

    private fun setImageOnAccountNameChange(imageID: Int) {
        binding.apply {
            accountLogo.apply {
                setImageResource(imageID)
                visibility = View.VISIBLE
            }
            remainingLayout.visibility = View.VISIBLE
        }
    }

    private fun initialization(container: ViewGroup?) {
        _binding = FragmentAddEditBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[AddEditViewModel::class.java]
        accountsViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().application)
        )[AccountsViewModel::class.java]
        firebaseDatabase = FirebaseDatabase.getInstance()
        //Top app bar
        (activity as MainActivity).supportActionBar?.hide()
        toolbarText = binding.root.findViewById(R.id.toolbar_title)
        toolbarDeleteIcon = binding.root.findViewById(R.id.rightIcon)
        //Mode = 0 -> Add | Mode = 1 -> Edit
        if (args.mode == 0)
            toolbarText.text = "Add account"
        else if (args.mode == 1) {
            toolbarText.text = "Edit account"
            toolbarDeleteIcon.visibility = View.VISIBLE
            binding.confirmChanges.text = "Save changes"
            args.accounts?.let {
                chooseAccount(it.accountName)
                binding.apply {
                    remainingLayout.visibility = View.VISIBLE
                    menuAccount.visibility = View.GONE
                    accountNameHeader.visibility = View.GONE
                    accountData.hint = it.accountData
                    confirmChanges.text = "Update changes"
                }
            }
        }
        toolbarBackButton = binding.root.findViewById(R.id.leftIcon)
        toolbarBackButton.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }


        // Account List
//        val accounts = resources.getStringArray(R.array.account_names)
        val accounts = Util.unusedAccounts
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            accounts
        )
        binding.accountName.setAdapter(arrayAdapter)
    }

    private fun AutoCompleteTextView.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }
}