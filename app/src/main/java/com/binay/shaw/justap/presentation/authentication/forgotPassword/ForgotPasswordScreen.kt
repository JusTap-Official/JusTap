package com.binay.shaw.justap.presentation.authentication.forgotPassword

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import com.binay.shaw.justap.presentation.authentication.FirebaseViewModel
import com.binay.shaw.justap.presentation.themes.DMSansFontFamily
import com.binay.shaw.justap.presentation.themes.JusTapTheme
import com.binay.shaw.justap.utilities.Util.findActivity
import com.binay.shaw.justap.utilities.Validator.Companion.isValidEmail
import com.binay.shaw.justap.utilities.onClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordScreen : ComponentActivity() {

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
                ForgotPasswordContent()
            }
        }
    }
}

@Composable
fun ForgotPasswordContent(
    modifier: Modifier = Modifier,
    firebaseViewModel: FirebaseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var userEmail by rememberSaveable { mutableStateOf("") }
    val enableSendEmailButton by remember { derivedStateOf { userEmail.isValidEmail() } }
    val resetPasswordRequestLiveData by firebaseViewModel.resetPasswordRequest.observeAsState()
    val errorLiveData by firebaseViewModel.errorLiveData.observeAsState()

    LaunchedEffect(resetPasswordRequestLiveData) {
        if (resetPasswordRequestLiveData == true) {
            context.findActivity()?.let {
                Toast.makeText(it, "Email sent", Toast.LENGTH_SHORT).show()
                it.finish()
            }
        }
    }

    LaunchedEffect(errorLiveData) {
        errorLiveData?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
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
                text = "Forgot Password", style = TextStyle(
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 36.sp
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

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    firebaseViewModel.resetPassword(userEmail)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                enabled = enableSendEmailButton
            ) {
                Text("Login", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

    }
}


//
//    private fun initObservers() {
//        firebaseViewModel.run {
//            resetPasswordRequest.observe(this@ForgotPasswordScreen) {
//                if (it) {
//                    goBackToSignInScreen()
//                }
//            }
//            errorLiveData.observe(this@ForgotPasswordScreen) {
//                stopProgress()
//                Toast.makeText(this@ForgotPasswordScreen, it.toString(), Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun goBackToSignInScreen() {
//        stopProgress()
//        Toast.makeText(this, R.string.email_sent, Toast.LENGTH_SHORT).show()
//        startActivity(Intent(this@ForgotPasswordScreen, SignInScreen::class.java))
//    }
//
//    private fun handleClicks() {
//        binding.btnResetPassword.progressButtonBg.setOnClickListener {
//            resetPassword()
//        }
//        binding.include.leftIcon.setOnClickListener {
//            onBackPressedDispatcher.onBackPressed()
//        }
//    }
//
//    private fun resetPassword() {
//        if (!Util.checkForInternet(this)) {
//            Util.showNoInternet(this)
//            return
//        }
//        showProgress()
//        val email = binding.etEmail.text.toString().trim()
//
//        val validation = Util.validateUserAuthInput(null, email, null)
//        if (validation < 7) {
//            stopProgress()
//            handleErrorInput(validation)
//            return
//        }
//
//        firebaseViewModel.resetPassword(email)
//    }
//
//    private fun handleErrorInput(validationCode: Int) {
//        binding.apply {
//            when (validationCode) {
//                2 -> {
//                    emailHelperTV.text = getString(R.string.enter_email)
//                    emailHelperTV.visibility = View.VISIBLE
//                }
//                3 -> {
//                    emailHelperTV.text = getString(R.string.invalid_email)
//                    emailHelperTV.visibility = View.VISIBLE
//                }
//            }
//        }
//    }
//
//
//    private fun showProgress() {
//        binding.apply {
//            emailHelperTV.visibility = View.GONE
//            btnResetPassword.apply {
//                buttonText.visibility = View.GONE
//                buttonProgress.visibility = View.VISIBLE
//            }
//        }
//    }
//
//    private fun stopProgress() {
//        binding.apply {
//            btnResetPassword.apply {
//                buttonText.visibility = View.VISIBLE
//                buttonProgress.visibility = View.GONE
//            }
//        }
//    }
//
//    private fun initialization() {
//        supportActionBar?.hide()
//        binding.apply {
//            include.apply {
//                toolbarTitle.text = getString(R.string.forgot_password_title)
//                leftIcon.visibility = View.VISIBLE
//            }
//            btnResetPassword.buttonText.text = resources.getString(R.string.SendResetLink)
//        }
//    }
//}