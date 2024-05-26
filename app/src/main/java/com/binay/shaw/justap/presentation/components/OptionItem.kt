package com.binay.shaw.justap.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.binay.shaw.justap.presentation.account.AccountOptions
import com.binay.shaw.justap.presentation.themes.normal16

@Composable
fun OptionItem(
    accountOptions: AccountOptions,
    modifier: Modifier = Modifier,
    onClick: (AccountOptions) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(accountOptions) }
            .padding(16.dp)
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            imageVector = accountOptions.icon,
            contentDescription = accountOptions.displayName,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )

        Text(
            text = accountOptions.displayName,
            color = MaterialTheme.colorScheme.onSurface,
            style = normal16
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsItemPreview() {
    OptionItem(AccountOptions.EDIT_PROFILE) {
        // Do something
    }
}