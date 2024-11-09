package com.binay.shaw.justap.presentation.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.binay.shaw.justap.domain.Resource
import com.binay.shaw.justap.presentation.components.AccountCard
import com.binay.shaw.justap.presentation.components.EmptyState
import com.binay.shaw.justap.presentation.components.ProgressDialog
import com.binay.shaw.justap.presentation.components.SearchBar
import com.binay.shaw.justap.presentation.mainScreens.homeScreen.accountFragments.AddEditViewModel
import com.binay.shaw.justap.presentation.navigation.LocalNavHost
import com.binay.shaw.justap.presentation.navigation.Screens
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.theapache64.rebugger.Rebugger
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    localUserViewModel: LocalUserViewModel = hiltViewModel(),
    accountViewModel: AccountsViewModel = hiltViewModel(),
    addEditViewModel: AddEditViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val navController = LocalNavHost.current

    val userAccountList by accountViewModel.userAccountList.collectAsState(emptyList())
    val user by localUserViewModel.user.collectAsState()
    val updateAccountResult by addEditViewModel.updateAccountResult.collectAsState()

    var showProgress by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    val filteredAccountList = if (search.isBlank()) {
        userAccountList
    } else {
        userAccountList.filter {
            it.accountName.contains(search, ignoreCase = true) ||
                    it.accountData.contains(search, ignoreCase = true)
        }
    }

    LaunchedEffect(updateAccountResult) {
        when (updateAccountResult) {
            is Resource.Success<*> -> {
                Toast.makeText(context, "Account updated successfully", Toast.LENGTH_SHORT)
                    .show()
                showProgress = false
                addEditViewModel.clearUpdateStatus()
            }

            is Resource.Error -> {
                Toast.makeText(context, "Failed to update account", Toast.LENGTH_SHORT).show()
                showProgress = false
            }

            is Resource.Loading -> {
                Timber.d("Updating account")
                showProgress = true
                addEditViewModel.clearUpdateStatus()
            }

            is Resource.Clear -> {
                showProgress = false
            }
        }
    }

    Rebugger(
        trackMap = mapOf(
            "userAccountList" to userAccountList,
            "user" to user,
            "search" to search,
            "filteredAccountList" to filteredAccountList
        )
    )

    LaunchedEffect(Unit) {
        accountViewModel.getAllUserAccounts()
        localUserViewModel.getUser()
    }

    LaunchedEffect(userAccountList) {
        Timber.d("User account list: $userAccountList")
    }

    if (showProgress) {
        ProgressDialog {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hi ${user.userName}",
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screens.ContactDetailsScreen.name)
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add account")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {

            SearchBar(
                search = search,
                onValueChange = { search = it },
                onSearchClick = {}
            )

            Spacer(Modifier.height(16.dp))

            if (userAccountList.isEmpty()) {
                EmptyState(showTextButton = true) {
                    // Navigate to add account screen
                    navController.navigate(Screens.ContactDetailsScreen.name)
                }
            } else if (filteredAccountList.isEmpty()) {
                EmptyState(text = "No accounts found")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = filteredAccountList,
                        key = { account -> account.accountID }
                    ) { account ->
                        AccountCard(account) { newValue ->
                            Timber.d("Switch clicked with value: $newValue")
                            addEditViewModel.updateVisibility(user.userID, account, newValue)
                        }
                    }
                }
            }
        }
    }
}