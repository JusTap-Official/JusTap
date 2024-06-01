package com.binay.shaw.justap.presentation.home

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.binay.shaw.justap.presentation.components.AccountCard
import com.binay.shaw.justap.presentation.components.EmptyState
import com.binay.shaw.justap.presentation.components.SearchBar
import com.binay.shaw.justap.presentation.mainScreens.homeScreen.accountFragments.AddEditViewModel
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    localUserViewModel: LocalUserViewModel = hiltViewModel(),
    accountViewModel: AccountsViewModel = hiltViewModel(),
    addEditViewModel: AddEditViewModel = hiltViewModel()
) {

    val userAccountList by accountViewModel.userAccountList.collectAsState(emptyList())
    val user by localUserViewModel.user.collectAsState()

    var search by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        accountViewModel.getAllUserAccounts()
        localUserViewModel.getUser()
    }

    LaunchedEffect(userAccountList) {
        Timber.d("User account list: $userAccountList")
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
                    // Navigate to add account screen
                    Timber.d("Add account clicked")
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
                onSearchClick = {
                    // Handle search click
                    Timber.d("Search clicked with value: $search")
                }
            )

            Spacer(Modifier.height(16.dp))

            if (userAccountList.isEmpty()) {
                Timber.d("User account list is empty")
                EmptyState(showTextButton = true)  {
                    // Navigate to add account screen
                    Timber.d("Add account clicked")
                }

            } else {
                Timber.d("User account list is not empty")

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = userAccountList,
                        key = { account -> account.accountID }
                    ) { account ->
                        AccountCard(account) { newValue ->
                            Timber.d("Switch clicked with value: $newValue")
                        }
                    }
                }
            }
        }
    }
}