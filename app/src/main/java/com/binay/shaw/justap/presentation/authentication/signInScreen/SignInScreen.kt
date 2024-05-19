package com.binay.shaw.justap.presentation.authentication.signInScreen


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.binay.shaw.justap.presentation.MainActivity
import com.binay.shaw.justap.presentation.authentication.ForgotPasswordScreen
import com.binay.shaw.justap.presentation.themes.DMSansFontFamily
import com.binay.shaw.justap.presentation.themes.JusTapTheme
import com.binay.shaw.justap.utilities.Util.findActivity
import com.binay.shaw.justap.utilities.Validator.Companion.isValidEmail
import com.binay.shaw.justap.utilities.Validator.Companion.isValidPassword
import com.binay.shaw.justap.utilities.onClick
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.viewModel.FirebaseViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(), Color.Transparent.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(), Color.Transparent.toArgb()
            )
        )
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            JusTapTheme {
                SignInScreenContent()
            }
        }
    }
}

@Composable
fun SignInScreenContent(
    modifier: Modifier = Modifier,
    accountsViewModel: AccountsViewModel = hiltViewModel(),
    firebaseViewModel: FirebaseViewModel = hiltViewModel(),
    localUserViewModel: LocalUserViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var userEmail by rememberSaveable { mutableStateOf("") }
    var userPassword by rememberSaveable { mutableStateOf("") }
    val enableLoginButton by remember {
        derivedStateOf {
            userEmail.isValidEmail() && userPassword.isValidPassword()
        }
    }
    val userLiveData by firebaseViewModel.userLiveData.observeAsState()

    LaunchedEffect(userLiveData) {

        userLiveData?.let { user ->
            val listAccounts = firebaseViewModel.accountsLiveData.value
            localUserViewModel.insertUser(user)

            listAccounts?.let { accounts ->
                accounts.forEach { account ->
                    accountsViewModel.insertAccount(account)
                }
            }

            val intent = Intent(context, MainActivity::class.java)
            context.findActivity()?.let {
                it.startActivity(intent)
                it.finish()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .then(modifier)
    ) {

        Column {

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Sign In", style = TextStyle(
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 48.sp
                )
            )

            Spacer(Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = "Mail outline icon"
                )
                OutlinedTextField(
                    value = userEmail,
                    onValueChange = { userEmail = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    placeholder = { Text(text = "johndoe123@gmail.com") }
                )
            }

            Spacer(Modifier.height(20.dp))

            Row {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "Key outline icon"
                )
                OutlinedTextField(
                    value = userPassword,
                    onValueChange = { userPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    placeholder = { Text(text = "johndoe123@gmail.com") }
                )
            }

            Spacer(Modifier.height(12.dp))

            Text("Forgot Password?", modifier = Modifier
                .align(Alignment.End)
                .onClick {
                    context.findActivity()?.let {
                            it.startActivity(Intent(it, ForgotPasswordScreen::class.java))
                        }
                })

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    firebaseViewModel.logInUser(
                        userEmail, userPassword
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                enabled = enableLoginButton
            ) {
                Text("Login", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

    }
}