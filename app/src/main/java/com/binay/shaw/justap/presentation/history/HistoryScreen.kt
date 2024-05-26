package com.binay.shaw.justap.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.binay.shaw.justap.presentation.mainScreens.historyScreen.LocalHistoryViewModel
import com.binay.shaw.justap.presentation.themes.medium20

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    historyViewModel: LocalHistoryViewModel = hiltViewModel()
) {

    val historyList by historyViewModel.getAllHistory.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
        Text(text = "This is history screen", style = medium20.copy(color = MaterialTheme.colorScheme.onPrimaryContainer))

        LazyColumn {
            items(historyList) {
                Text("Name: ${it.username}")
            }
        }
    }
}