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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentAddEditBinding
import com.binay.shaw.justap.databinding.ParagraphModalBinding
import com.binay.shaw.justap.helper.Util.Companion.createBottomSheet
import com.binay.shaw.justap.helper.Util.Companion.setBottomSheet
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initialization(container)


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
                lifecycleScope.launch {
                    selectedAccount?.let { it1 ->
                        getStringIndex(it1)
                    }?.let { index ->
                        if (args.mode == 0) {
                            viewModel.saveData(firebaseDatabase, args.userID, index, selectedAccount!!, accountData)
                            viewModel.status.observe(viewLifecycleOwner) {
                                if (it == 1) {
                                    Toast.makeText(requireContext(), "Saved Data", Toast.LENGTH_SHORT).show()
                                    requireActivity().onBackPressedDispatcher.onBackPressed()
                                } else if (it == 2) {
                                    Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else
                            viewModel.updateData(firebaseDatabase, args.userID, index, selectedAccount!!, accountData)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Fill all input fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }



        return binding.root
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
        toolbarBackButton.visibility = View.VISIBLE

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