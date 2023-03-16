package com.binay.shaw.justap.ui.mainScreens.homeScreen.accountFragments

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentAddEditBinding
import com.binay.shaw.justap.databinding.MyToolbarBinding
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.databinding.ParagraphModalBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.createBottomSheet
import com.binay.shaw.justap.helper.Util.setBottomSheet
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.model.Accounts
import com.google.firebase.database.FirebaseDatabase
import com.tapadoo.alerter.Alerter


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
        initObservers()

        clickHandlers()

        return binding.root
    }

    private fun initObservers() {
        viewModel.run {

            updateStatus.observe(viewLifecycleOwner) { status ->
                if (status == 3) {
                    Util.log("Status value = $status")
                    updateStatus.postValue(0)
                    Alerter.create(requireActivity())
                        .setTitle(resources.getString(R.string.data_updated_successfully))
                        .setBackgroundColorInt(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.positive_green
                            )
                        )
                        .setIcon(R.drawable.check)
                        .setDuration(800L)
                        .show()
                    binding.progressAnimation.progressParent.visibility = View.GONE
                    findNavController().navigateUp()
                }
            }

            deleteStatus.observe(viewLifecycleOwner) { status ->
                if (status == 3) {
                    Util.log("Status value = $status")
                    deleteStatus.postValue(0)
                    Alerter.create(requireActivity())
                        .setTitle(resources.getString(R.string.data_deleted_successfully))
                        .setBackgroundColorInt(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.negative_red
                            )
                        )
                        .setIcon(R.drawable.delete)
                        .setDuration(800L)
                        .show()
                    binding.progressAnimation.progressParent.visibility = View.GONE
                    findNavController().navigateUp()
                }
            }

            saveStatus.observe(viewLifecycleOwner) {
                if (it == 3) {
                    saveStatus.value = 0
                    //Success
                    Alerter.create(requireActivity())
                        .setTitle(resources.getString(R.string.data_saved_successfully))
                        .setBackgroundColorInt(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.positive_green
                            )
                        )
                        .setIcon(R.drawable.check)
                        .setDuration(800L)
                        .show()
                    binding.progressAnimation.progressParent.visibility =
                        View.GONE
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }

    }

    private fun clickHandlers() {

        onCancelChangesHandler()
        onInfoHandler()
        onDeleteAccountHandler()
        onAccountNameEntered()
        onConfirmHandler()
    }

    private fun onConfirmHandler() {
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
    }

    private fun onAccountNameEntered() {
        binding.apply {
            accountName.afterTextChanged {
                accountData.setText("")
                selectedAccount = it
                chooseAccount(it)
            }
        }
    }

    private fun onDeleteAccountHandler() {
        toolBar.rightIcon.setOnClickListener {
            deleteAccount()
        }
    }

    private fun onInfoHandler() {
        binding.info.setOnClickListener {
            val dialog = ParagraphModalBinding.inflate(layoutInflater)
            val bottomSheet = requireActivity().createBottomSheet()
            dialog.apply {
                paragraphHeading.text = resources.getString(R.string.EnterURLorUsername)
                paragraphContent.text =
                    resources.getString(R.string.AddEditFragmentBottomModalDescription)
                paragraphContent.gravity = Gravity.CENTER
            }
            dialog.root.setBottomSheet(bottomSheet)
        }
    }

    private fun onCancelChangesHandler() {
        binding.cancelChanges.setOnClickListener {
            handleBackButtonPress()
        }
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
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                negativeOption.setOnClickListener {
                    bottomSheet.dismiss()
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
                    makeUpdateAccountRequest(newData)
                } else {
                    Util.showNoInternet(requireActivity())
                    return@setOnClickListener
                }
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
            }
            dialog.root.setBottomSheet(bottomSheet)
        }

    }

    private fun makeUpdateAccountRequest(newData: String) {
        args.accounts?.let {
            val array = resources.getStringArray(R.array.account_names)
            val index = array.indexOf(it.accountName)
            it.accountID = index
            it.accountData = newData

            viewModel.updateEntry(accountsViewModel, firebaseDatabase, it)
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
                    makeDeleteAccountRequest()
                } else {
                    Util.showNoInternet(requireActivity())
                    return@setOnClickListener
                }
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
            }
            dialog.root.setBottomSheet(bottomSheet)
        }
    }

    private fun makeDeleteAccountRequest() {
        args.accounts?.let {
            val array = resources.getStringArray(R.array.account_names)
            val index = array.indexOf(it.accountName)
            it.accountID = index

            viewModel.deleteEntry(accountsViewModel, firebaseDatabase, it)
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
                    makeSaveRequest(accountData)
                } else {
                    Util.showNoInternet(requireActivity())
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

    private fun makeSaveRequest(accountData: String) {

        selectedAccount?.let { it1 ->
            getStringIndex(it1)
        }?.let { index ->

            val account = Accounts(index, selectedAccount!!, accountData, true)

            //Save new Data
            viewModel.saveData(
                accountsViewModel,
                firebaseDatabase,
                Util.userID,
                account
            )
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
        binding.apply {
            val accountDataTvString = StringBuilder()
            accountDataTvString.append(resources.getString(R.string.enter))

            when (it) {
                resources.getStringArray(R.array.account_names)[0] -> {
                    accountData.apply {
                        inputType = InputType.TYPE_CLASS_PHONE
                        hint = resources.getString(R.string.dummy_phone_number)
                    }
                    accountDataTvString.append(it).append(resources.getString(R.string.number))
                    accountInputTV.text = accountDataTvString.toString()
                }
                resources.getStringArray(R.array.account_names)[1] -> {
                    accountData.apply {
                        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        hint = resources.getString(R.string.dummy_email)
                    }
                    accountDataTvString.append(it)
                    binding.accountInputTV.text = accountDataTvString.toString()
                }
                resources.getStringArray(R.array.account_names)[16] -> {
                    accountData.apply {
                        inputType = InputType.TYPE_CLASS_PHONE
                        hint = resources.getString(R.string.dummy_phone_number)
                    }
                    accountDataTvString.append(it).append(resources.getString(R.string.number))
                    accountInputTV.text = accountDataTvString.toString()
                }
                else -> {
                    accountData.apply {
                        inputType = InputType.TYPE_CLASS_TEXT
                        hint = resources.getString(R.string.username123)
                    }
                    accountDataTvString.append(it).append(resources.getString(R.string.url_username))
                    accountInputTV.text = accountDataTvString.toString()
                }
            }

        }
    }
}