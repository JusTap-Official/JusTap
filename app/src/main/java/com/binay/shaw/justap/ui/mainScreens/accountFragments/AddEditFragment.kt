package com.binay.shaw.justap.ui.mainScreens.accountFragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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


        binding.accountName.afterTextChanged {
            selectedAccount = it
            when (it) {
                "Phone" -> setImageOnAccountNameChange(R.drawable.phone)
                "Email" -> setImageOnAccountNameChange(R.drawable.email)
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
                "WhatsApp" -> setImageOnAccountNameChange(R.drawable.whatsapp)
            }
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

    private fun saveData(accountData: String) {

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text = requireContext().resources.getString(R.string.ConfirmChanges)
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
                                args.userID,
                                index,
                                selectedAccount!!,
                                accountData
                            )
                            viewModel.status.observe(viewLifecycleOwner) {
                                if (it == 1) {
                                    //Success
                                    Toast.makeText(
                                        requireContext(),
                                        "Saved Data",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding.progressAnimation.progressParent.visibility = View.GONE
                                    requireActivity().onBackPressedDispatcher.onBackPressed()
                                } else if (it == 2) {
                                    //Fail
                                    binding.progressAnimation.progressParent.visibility = View.GONE
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to add",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            //Update current Data
                            viewModel.updateData(
                                firebaseDatabase,
                                args.userID,
                                index,
                                selectedAccount!!,
                                accountData
                            )
                        }
                    }
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
        binding.accountLogo.apply {
            setImageResource(imageID)
            visibility = View.VISIBLE
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
        //Mode = 0 -> Add | Mode = 1 -> Edit
        if (args.mode == 0)
            toolbarText.text = "Add account"
        else {
            toolbarText.text = "Edit account"
            toolbarDeleteIcon = binding.root.findViewById(R.id.rightIcon)
            toolbarDeleteIcon.visibility = View.VISIBLE
            binding.confirmChanges.text = "Save changes"
        }
        toolbarBackButton = binding.root.findViewById(R.id.leftIcon)
        toolbarBackButton.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }


//        accountNameList = Arrays.asList(resources.getStringArray(R.array.account_names))
        // Account List
        val accounts = resources.getStringArray(R.array.account_names)
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
}