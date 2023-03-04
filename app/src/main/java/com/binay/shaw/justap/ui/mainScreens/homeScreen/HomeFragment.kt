package com.binay.shaw.justap.ui.mainScreens.homeScreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.AccountsItemAdapter
import com.binay.shaw.justap.base.BaseFragment
import com.binay.shaw.justap.databinding.FragmentHomeBinding
import com.binay.shaw.justap.databinding.MyToolbarBinding
import com.binay.shaw.justap.databinding.ParagraphModalBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.createBottomSheet
import com.binay.shaw.justap.helper.Util.setBottomSheet
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.ui.mainScreens.homeScreen.accountFragments.AddEditViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.google.firebase.database.FirebaseDatabase


@SuppressLint("SetTextI18n", "NotifyDataSetChanged")
class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var localUserViewModel: LocalUserViewModel
    private lateinit var accountsViewModel: AccountsViewModel
    private lateinit var localUser: LocalUser
    private var fabTitle: TextView? = null
    private var accountsList = mutableListOf<Accounts>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: AccountsItemAdapter
    private lateinit var toolBar: MyToolbarBinding
    private lateinit var addEditViewModel: AddEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialization(container)

        initObservers()
        initView()

        return binding.root
    }

    private fun initObservers() {
        addEditViewModel.updateStatus.observe(viewLifecycleOwner) {
            if (it == 3) {
                Toast.makeText(requireContext(), resources.getString(R.string.data_added_successfully), Toast.LENGTH_SHORT).show()
                addEditViewModel.updateStatus.value = 0
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initView() {
        binding.fabLayout.setOnClickListener {
            gotoAddAccountFragment()
        }

        binding.fabCircle.setOnClickListener {
            gotoAddAccountFragment()
        }

        toolBar.rightIcon.setOnClickListener {
            showAccountInfoDialog()
        }
    }

    private fun showAccountInfoDialog() {
        val dialog = ParagraphModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {
            paragraphHeading.text = resources.getString(R.string.HowToUse)
            paragraphContent.text = resources.getString(R.string.HowToUseDescription)
            paragraphImageView.apply {
                setImageResource(R.drawable.account_item_info)
                visibility = View.VISIBLE
            }
        }
        dialog.root.setBottomSheet(bottomSheet)
    }

    private fun gotoAddAccountFragment() {
        getUnusedAccountsList()
        if (Util.unusedAccounts.size == 0) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.youve_used_all_accounts),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val action = HomeFragmentDirections.actionHomeToAddEditFragment(0)
        findNavController().navigate(action)
        binding.fabLayout.visibility = View.GONE
    }

    private fun getUnusedAccountsList() {
        val unusedAccounts = mutableListOf<String>()
        val usedAccounts = mutableListOf<String>()

        accountsViewModel.getAllUser.observe(viewLifecycleOwner) {
            val allAccounts = resources.getStringArray(R.array.account_names)
            for (l in it) {
                usedAccounts.add(l.accountName)
            }

            for (account in allAccounts) {
                if (!usedAccounts.contains(account))
                    unusedAccounts.add(account)
            }
        }
        Util.unusedAccounts = unusedAccounts
    }

    private fun initialization(container: ViewGroup?) {

        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        toolBar = binding.include
        toolBar.toolbarTitle.text = resources.getString(R.string.Home)
        toolBar.rightIcon.apply {
            setImageResource(R.drawable.info_icon)
            visibility = View.VISIBLE
        }
        fabTitle = binding.fabText

        recyclerViewAdapter = AccountsItemAdapter(requireActivity()) { newAccount ->
            for (account in accountsList) {
                if (account.accountID == newAccount.accountID) {
                    val currentState = account.showAccount
                    account.showAccount = currentState.not()

                    addEditViewModel.updateEntry(
                        accountsViewModel,
                        FirebaseDatabase.getInstance(),
                        account
                    )
                }
            }
        }

        recyclerView = binding.accountsRv
        recyclerView.apply {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        addEditViewModel = ViewModelProvider(
            this
        )[AddEditViewModel::class.java]

        localUserViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[LocalUserViewModel::class.java]

        accountsViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[AccountsViewModel::class.java]

        localUserViewModel.fetchUser.observe(viewLifecycleOwner) {
            localUser = LocalUser(
                it.userID,
                it.userName,
                it.userEmail,
                it.userBio,
                it.userProfilePicture,
                it.userBannerPicture
            )
            Util.userID = it.userID
            binding.profileNameTV.text = "Hi ${Util.getFirstName(localUser.userName)},"
            binding.profileBioTV.text = localUser.userBio
            val profileURL = localUser.userProfilePicture!!
            if (profileURL.isNotEmpty()) {
                Util.loadImagesWithGlide(binding.profileImage, profileURL)
            }
        }

        accountsViewModel.getAllUser.observe(viewLifecycleOwner) {
            Util.log(it.toString())
            accountsList.clear()
            accountsList.addAll(it)
            recyclerViewAdapter.setData(it)
            recyclerViewAdapter.notifyDataSetChanged()
        }
        handleOnScrollFAB()
    }

    private fun handleOnScrollFAB() {
        binding.accountsRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding.fabText.visibility == View.VISIBLE) {
                    binding.fabText.visibility = View.GONE
                } else if (dy < 0 && binding.fabText.visibility != View.VISIBLE) {
                    binding.fabText.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        recyclerViewAdapter.notifyDataSetChanged()
    }
}