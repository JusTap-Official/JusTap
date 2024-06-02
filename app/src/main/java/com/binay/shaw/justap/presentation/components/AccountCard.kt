package com.binay.shaw.justap.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.utilities.Util

@Composable
fun AccountCard(
    account: Accounts,
    modifier: Modifier = Modifier,
    onSwitchChange: (Boolean) -> Unit
) {
    var isSwitchChecked by remember { mutableStateOf(account.showAccount) }
    val accountIcon = Util.getImageDrawableFromAccountName(account.accountName)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(accountIcon)
                    .build(), contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = account.accountName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = account.accountData,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Switch(
                checked = isSwitchChecked,
                onCheckedChange = {
                    isSwitchChecked = it
                    onSwitchChange(it)
                },
//                colors = SwitchDefaults.colors(
//                    checkedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
//                    uncheckedThumbColor = MaterialTheme.colorScheme.surface,
//                    checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
//                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceDim
//                )
            )
        }
    }
}
