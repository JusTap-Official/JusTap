package com.binay.shaw.justap.presentation.authentication.signInScreen


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.binay.shaw.justap.R
import com.binay.shaw.justap.presentation.MainActivity
import com.binay.shaw.justap.presentation.authentication.FirebaseViewModel
import com.binay.shaw.justap.presentation.authentication.forgotPassword.ForgotPasswordScreen
import com.binay.shaw.justap.presentation.authentication.signUpScreen.SignUpScreen
import com.binay.shaw.justap.presentation.components.AuthSwitcher
import com.binay.shaw.justap.presentation.components.MyButton
import com.binay.shaw.justap.presentation.components.ProgressDialog
import com.binay.shaw.justap.presentation.themes.DMSansFontFamily
import com.binay.shaw.justap.presentation.themes.JusTapTheme
import com.binay.shaw.justap.presentation.themes.bold36
import com.binay.shaw.justap.utilities.Util.findActivity
import com.binay.shaw.justap.utilities.Util.isNetworkAvailable
import com.binay.shaw.justap.utilities.Validator.Companion.isValidEmail
import com.binay.shaw.justap.utilities.Validator.Companion.isValidPassword
import com.binay.shaw.justap.utilities.onClick
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            JusTapTheme {
                SignInScreenUi()
            }
        }
    }
}

@Composable
fun SignInScreenUi(modifier: Modifier = Modifier) {

    val colorsList = listOf(
        Color(0xFF2878D1),
        Color(0xFF2F64B6),
        Color(0xFF234DA0),
        Color(0xFF172D85),
        Color(0xFF0D196C)
    )

    val infiniteTransition = rememberInfiniteTransition(label = "infine_transition")
    val context = LocalContext.current

    var currentIndex by remember { mutableIntStateOf(0) }
    var direction by remember { mutableIntStateOf(1) }

    val nextIndex = (currentIndex + direction).coerceIn(0, colorsList.size - 1)

    val animatedColor by infiniteTransition.animateColor(
        initialValue = colorsList[currentIndex],
        targetValue = colorsList[nextIndex],
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "animated_background"
    )

    LaunchedEffect(animatedColor) {
        if (animatedColor == colorsList[nextIndex]) {
            currentIndex = nextIndex
            if (currentIndex == 0 || currentIndex == colorsList.size - 1) {
                direction *= -1
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(animatedColor).then(modifier),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.blob_background),
            contentDescription = null,
            modifier = Modifier.alpha(0.1f)
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Continue to\n" + "your account.",
                style = bold36,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)).background(
                    Color.White
                ).padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.End
            ) {

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = "userEmail",
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = MaterialTheme.shapes.extraLarge,
                    placeholder = { Text(text = stringResource(R.string.enter_your_email)) }
                )

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = "userPassword",
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    placeholder = { Text(text = stringResource(R.string.enter_your_password)) },
                    trailingIcon = {
                        val image = if (true)
                            Icons.Filled.VisibilityOff
                        else Icons.Filled.Visibility

                        val description =
                            if (true) stringResource(R.string.show_password)
                            else stringResource(R.string.hide_password)

                        IconButton(onClick = { }) {
                            Icon(imageVector = image, description)
                        }
                    }
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.forgot_password), modifier = Modifier,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(20.dp))

                MyButton(
                    text = stringResource(R.string.login),
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth().padding(vertical = 20.dp)
                ) {
                }

                AuthSwitcher(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.don_t_have_an_account),
                    clickableText = stringResource(id = R.string.sign_up)
                ) {
                    context.findActivity()?.run {
                        startActivity(Intent(this, SignUpScreen::class.java))
                    }
                }
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

    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val enableLoginButton by remember {
        derivedStateOf {
            userEmail.isValidEmail() && userPassword.isValidPassword()
        }
    }
    val userLiveData by firebaseViewModel.userLiveData.observeAsState()
    val errorLiveData by firebaseViewModel.errorLiveData.observeAsState()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(userLiveData) {

        userLiveData?.let { user ->
            val listAccounts = firebaseViewModel.accountsLiveData.value
            localUserViewModel.insertUser(user)

            listAccounts?.let { accounts ->
                accounts.forEach { account ->
                    accountsViewModel.insertAccount(account)
                }
            }
            isLoading = false
            val intent = Intent(context, MainActivity::class.java)
            context.findActivity()?.let {
                it.startActivity(intent)
                it.finish()
            }
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
                text = stringResource(R.string.signin), style = TextStyle(
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

                        val description =
                            if (passwordVisible) stringResource(R.string.show_password)
                            else stringResource(R.string.hide_password)

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.forgot_password), modifier = Modifier
                    .align(Alignment.End)
                    .onClick {
                        context
                            .findActivity()
                            ?.let {
                                it.startActivity(Intent(it, ForgotPasswordScreen::class.java))
                            }
                    },
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.weight(1f))

            MyButton(
                text = stringResource(R.string.login),
                enabled = enableLoginButton,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End)
                    .padding(vertical = 20.dp)
            ) {
                if (context.isNetworkAvailable()) {
                    isLoading = true
                    firebaseViewModel.logInUser(
                        userEmail, userPassword
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
                text = stringResource(id = R.string.don_t_have_an_account),
                clickableText = stringResource(id = R.string.sign_up)
            ) {
                context.findActivity()?.run {
                    startActivity(Intent(this, SignUpScreen::class.java))
                }
            }
        }
    }
}