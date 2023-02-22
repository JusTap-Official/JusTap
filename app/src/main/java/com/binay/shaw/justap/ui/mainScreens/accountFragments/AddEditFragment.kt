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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentAddEditBinding
import com.binay.shaw.justap.databinding.MyToolbarBinding
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.databinding.ParagraphModalBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.Companion.createBottomSheet
import com.binay.shaw.justap.helper.Util.Companion.setBottomSheet
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.viewModel.AddEditViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.tapadoo.alerter.Alerter
import kotlinx.coroutines.launch

class AddEditFragment : Fragment() {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!
    private val args: AddEditFragmentArgs by navArgs()
    private var selectedAccount: String? = null
    private lateinit var viewModel: AddEditViewModel
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var accountsViewModel: AccountsViewModel
    private lateinit var toolBar: MyToolbarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddEditBinding.inflate(layoutInflater, container, false)
        initialization()

        binding.cancelChanges.setOnClickListener {
            handleBackButtonPress()
        }

        binding.info.setOnClickListener {
            val dialog = ParagraphModalBinding.inflate(layoutInflater)
            val bottomSheet = requireActivity().createBottomSheet()
            dialog.apply {
                paragraphHeading.text = resources.getString(R.string.EnterURLorUsername)
                paragraphContent.text =
                    resources.getString(R.string.AddEditFragmentBottomModalDescription)
            }
            dialog.root.setBottomSheet(bottomSheet)
        }

        toolBar.rightIcon.setOnClickListener {
            deleteAccount()
        }

        binding.accountName.afterTextChanged {
            selectedAccount = it
            chooseAccount(it)
        }

        binding.confirmChanges.setOnClickListener {
            if (binding.confirmChanges.text.equals("Add account")) {
                val accountData = binding.accountData.text.toString()
                if (dataIsValid(selectedAccount, accountData)) {
                    saveData(accountData)
                } else {
                    Toast.makeText(requireContext(), "Fill all input fields", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
            } else {
                val newData = binding.accountData.text.toString()
                if (newData.isNotEmpty()) {
                    updateData(newData)
                } else {
                    Toast.makeText(requireContext(), "Fill all input fields", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
            }
        }

        return binding.root
    }

    private fun handleBackButtonPress() {
        val inputName = binding.accountData.text.toString().trim()

        if (inputName.isNotEmpty()) {

            val dialog = OptionsModalBinding.inflate(layoutInflater)
            val bottomSheet = requireContext().createBottomSheet()
            dialog.apply {

                optionsHeading.text = requireContext().resources.getString(R.string.DiscardChanged)
                optionsContent.text =
                    requireContext().resources.getString(R.string.DiscardChangedDescription)
                positiveOption.text = requireContext().resources.getString(R.string.Discard)
                positiveOption.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.negative_red
                    )
                )
                negativeOption.text = requireContext().resources.getString(R.string.ContinueEditing)
                negativeOption.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_color
                    )
                )
                positiveOption.setOnClickListener {
                    bottomSheet.dismiss()
                    Util.log("Go back")
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                negativeOption.setOnClickListener {
                    bottomSheet.dismiss()
                    Util.log("Stay")
                }
            }
            dialog.root.setBottomSheet(bottomSheet)
        } else
            requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun updateData(newData: String) {

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text = resources.getString(R.string.UpdateChanges)
            optionsContent.text = resources.getString(R.string.AreYouSureYouWantToUpdateThisAccount)
            positiveOption.text = resources.getString(R.string.Update)
            positiveOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.negative_red
                )
            )
            negativeOption.text = resources.getString(R.string.DontUpdate)
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
                        it.accountData = newData
                        lifecycleScope.launch {

                            viewModel.updateEntry(accountsViewModel, firebaseDatabase, it)

                            viewModel.updateStatus.observe(viewLifecycleOwner) { status ->
                                if (status == 3) {
                                    Util.log("Status value = $status")
                                    viewModel.updateStatus.postValue(0)
                                    Snackbar.make(binding.root, "Data updated successfully", Snackbar.LENGTH_SHORT).show()
                                    binding.progressAnimation.progressParent.visibility = View.GONE
                                    findNavController().navigateUp()
                                }
                            }
                        }
                    }
                } else {
                    Alerter.create(requireActivity())
                        .setTitle(resources.getString(R.string.noInternet))
                        .setText(resources.getString(R.string.noInternetDescription))
                        .setBackgroundColorInt(ContextCompat.getColor(requireContext(), R.color.negative_red))
                        .setIcon(R.drawable.wifi_off)
                        .setDuration(2000L)
                        .show()
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

    private fun deleteAccount() {

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text = requireContext().resources.getString(R.string.ConfirmChanges)
            optionsContent.text = resources.getString(R.string.AreYouSureYouWantToDeleteThisAccount)
            positiveOption.text = resources.getString(R.string.Delete)
            positiveOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.negative_red
                )
            )
            negativeOption.text = resources.getString(R.string.DontDelete)
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
                                    Snackbar.make(
                                        binding.root,
                                        "Successfully Deleted",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    binding.progressAnimation.progressParent.visibility = View.GONE
                                    findNavController().navigateUp()
                                }
                            }
                        }
                    }
                } else {
                    Alerter.create(requireActivity())
                        .setTitle(resources.getString(R.string.noInternet))
                        .setText(resources.getString(R.string.noInternetDescription))
                        .setBackgroundColorInt(ContextCompat.getColor(requireContext(), R.color.negative_red))
                        .setIcon(R.drawable.wifi_off)
                        .setDuration(2000L)
                        .show()
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
                                    Snackbar.make(
                                        binding.root,
                                        "Data saved successfully",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    binding.progressAnimation.progressParent.visibility =
                                        View.GONE
                                    requireActivity().onBackPressedDispatcher.onBackPressed()
                                }
                            }

                        }
                    }
                } else {
                    Alerter.create(requireActivity())
                        .setTitle(resources.getString(R.string.noInternet))
                        .setText(resources.getString(R.string.noInternetDescription))
                        .setBackgroundColorInt(ContextCompat.getColor(requireContext(), R.color.negative_red))
                        .setIcon(R.drawable.wifi_off)
                        .setDuration(2000L)
                        .show()
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
        if (selectedAccount.isNullOrEmpty() || accountData.isEmpty())
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

    private fun initialization() {
        viewModel = ViewModelProvider(requireActivity())[AddEditViewModel::class.java]
        accountsViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[AccountsViewModel::class.java]
        firebaseDatabase = FirebaseDatabase.getInstance()

        //Top app bar
        (activity as MainActivity).supportActionBar?.hide()
        toolBar = binding.include

        //Mode = 0 -> Add | Mode = 1 -> Edit
        if (args.mode == 0) {
            toolBar.toolbarTitle.text = resources.getString(R.string.AddAccount)
            toolBar.rightIcon.visibility = View.GONE
        } else if (args.mode == 1) {
            toolBar.toolbarTitle.text = resources.getString(R.string.EditAccount)
            toolBar.rightIcon.apply {
                setImageResource(R.drawable.delete)
                visibility = View.VISIBLE
            }
            args.accounts?.let {
                chooseAccount(it.accountName)
                binding.apply {
                    remainingLayout.visibility = View.VISIBLE
                    menuAccount.visibility = View.GONE
                    accountNameHeader.visibility = View.GONE
                    accountData.hint = it.accountData
                    confirmChanges.text = resources.getString(R.string.UpdateChanges)
                }
            }
        }

        toolBar.leftIcon.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                handleBackButtonPress()
            }
        }

        // Account List
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    private fun chooseAccount(it: String) {

        setImageOnAccountNameChange(Util.getImageDrawableFromAccountName(it))

        when (it) {
            "Phone" -> {
                binding.accountData.inputType = InputType.TYPE_CLASS_PHONE
            }
            "Email" -> {
                binding.accountData.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }

            "WhatsApp" -> {
                binding.accountData.inputType = InputType.TYPE_CLASS_PHONE
            }
        }
    }
}