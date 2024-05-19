package com.binay.shaw.justap.presentation.introduction

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.binay.shaw.justap.R
import com.binay.shaw.justap.presentation.MainActivity
import com.binay.shaw.justap.presentation.authentication.signInScreen.SignInScreen
import com.binay.shaw.justap.presentation.themes.JusTapTheme
import com.binay.shaw.justap.utilities.Util
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.White.toArgb(), Color.White.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.White.toArgb(), Color.White.toArgb()
            )
        )
        super.onCreate(savedInstanceState)

        setContent {
            JusTapTheme {
                SplashContent(activity = this@SplashScreen)
            }
        }
    }
}

@Composable
fun SplashContent(
    activity: Activity,
    modifier: Modifier = Modifier
) {

    var isUserLoggedIn: Boolean by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        isUserLoggedIn = Util.isUserLoggedIn()

        val activityToIntent = if (isUserLoggedIn) {
            MainActivity::class.java
        } else {
            SignInScreen::class.java
        }

        activity.run {
            delay(1_000L)
            startActivity(Intent(this, activityToIntent))
            finish()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize().then(modifier),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null
            )
        }
    }
}