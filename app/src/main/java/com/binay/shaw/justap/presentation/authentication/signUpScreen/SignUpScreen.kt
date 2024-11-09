package com.binay.shaw.justap.presentation.authentication.signUpScreen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.binay.shaw.justap.R
import com.binay.shaw.justap.presentation.authentication.FirebaseViewModel
import com.binay.shaw.justap.presentation.components.AuthSwitcher
import com.binay.shaw.justap.presentation.components.MyButton
import com.binay.shaw.justap.presentation.components.ProgressDialog
import com.binay.shaw.justap.presentation.themes.DMSansFontFamily
import com.binay.shaw.justap.presentation.themes.JusTapTheme
import com.binay.shaw.justap.utilities.Util.findActivity
import com.binay.shaw.justap.utilities.Util.isNetworkAvailable
import com.binay.shaw.justap.utilities.Validator.Companion.isValidEmail
import com.binay.shaw.justap.utilities.Validator.Companion.isValidName
import com.binay.shaw.justap.utilities.Validator.Companion.isValidPassword
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpScreen : ComponentActivity() {

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
                SignUpScreenContent()
            }
        }
    }
}

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    firebaseViewModel: FirebaseViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val enableSignUpButton by remember {
        derivedStateOf {
            userName.isValidName() && userEmail.isValidEmail() && userPassword.isValidPassword()
        }
    }

    val errorLiveData by firebaseViewModel.errorLiveData.observeAsState()
    val registerStatus by firebaseViewModel.registerStatus.observeAsState(false)
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(registerStatus) {
        if (registerStatus) {
            isLoading = false
            Toast.makeText(
                context,
                context.getString(R.string.account_created_successfully), Toast.LENGTH_SHORT
            ).show()
            context.findActivity()?.finish()
        }
    }

    LaunchedEffect(errorLiveData) {
        errorLiveData?.let { error ->
            isLoading = false
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    if (isLoading) {
        ProgressDialog {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .imePadding()
            .then(modifier)
    ) {

        Column {

            Spacer(Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.sign_up), style = TextStyle(
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 48.sp
                )
            )

            Spacer(Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Name outline icon"
                )
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    placeholder = { Text(text = stringResource(R.string.enter_your_name)) }
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = "Mail outline icon"
                )
                OutlinedTextField(
                    value = userEmail,
                    onValueChange = { userEmail = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = MaterialTheme.shapes.large,
                    placeholder = { Text(text = stringResource(R.string.enter_your_email)) }
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "Key outline icon"
                )
                OutlinedTextField(
                    value = userPassword,
                    onValueChange = { userPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    placeholder = { Text(text = stringResource(R.string.enter_your_password)) },
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.VisibilityOff
                        else Icons.Filled.Visibility

                        val description = if (passwordVisible) stringResource(R.string.show_password)
                        else stringResource(R.string.hide_password)

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    }
                )
            }

            Spacer(Modifier.weight(1f))

            MyButton(
                text = stringResource(R.string.createAccount),
                enabled = enableSignUpButton,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End)
                    .padding(vertical = 20.dp)
            ) {
                if (context.isNetworkAvailable()) {
                    isLoading = true
                    firebaseViewModel.createNewAccount(
                        userName, userEmail, userPassword
                    )
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.noInternet),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            AuthSwitcher(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.already_have_an_account),
                clickableText = stringResource(id = R.string.sign_in)
            ) {
                context.findActivity()?.run {
                    startActivity(Intent(this, SignUpScreen::class.java))
                }
            }
        }
    }
}