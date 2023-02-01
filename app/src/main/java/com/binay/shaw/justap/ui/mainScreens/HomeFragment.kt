package com.binay.shaw.justap.ui.mainScreens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.AccountsItemAdapter
import com.binay.shaw.justap.databinding.FragmentHomeBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var localUserViewModel: LocalUserViewModel
    private lateinit var accountsViewModel: AccountsViewModel
    private lateinit var localUser: LocalUser
    private var fabTitle: TextView? = null
    private lateinit var accountsList: List<Accounts>
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: AccountsItemAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialization(container)

        binding.fabLayout.setOnClickListener {
            gotoAddAccountFragment()
        }

        binding.fabCircle.setOnClickListener {
            gotoAddAccountFragment()
        }

        return binding.root
    }

    private fun gotoAddAccountFragment() {
        val action = HomeFragmentDirections.actionHomeToAddEditFragment(0)
        findNavController().navigate(action)
        binding.fabLayout.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun initialization(container: ViewGroup?) {

        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.root.findViewById<TextView>(R.id.toolbar_title)?.text = requireContext().resources.getString(R.string.Home)
        fabTitle = binding.fabText
        recyclerViewAdapter = AccountsItemAdapter(requireContext())
        recyclerView = binding.accountsRv
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        localUserViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[LocalUserViewModel::class.java]

        accountsViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[AccountsViewModel::class.java]

        localUserViewModel.fetchUser.observe(viewLifecycleOwner) {
            localUser = LocalUser(
                it.userID,
                it.userName,
                it.userEmail,
                it.userBio,
                it.userPhone,
                it.userProfilePicture,
                it.userBannerPicture
            )
            Util.userID = it.userID
            binding.profileNameTV.text  = "Hi ${localUser.userName.split(" ")[0]}"
            binding.profileBioTV.text = localUser.userBio
            val profileURL = localUser.userProfilePicture!!
            if (profileURL.isNotEmpty()) {
                Util.loadImagesWithGlide(binding.profileImage, profileURL)
            }

        }

        accountsViewModel.getAllUser.observe(viewLifecycleOwner, Observer {
            Util.log(it.toString())
            recyclerViewAdapter.setData(it)
            recyclerViewAdapter.notifyDataSetChanged()
        })

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
}