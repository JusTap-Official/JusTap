package com.binay.shaw.justap.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseActivity
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.helper.Constants
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.createBottomSheet
import com.binay.shaw.justap.helper.Util.setBottomSheet
import com.binay.shaw.justap.ui.authentication.signInScreen.SignInScreen
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var task: Task<AppUpdateInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        checkForUpdate()
//        makeIntent(Util.isUserLoggedIn())
    }

    private fun checkForUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        task = appUpdateManager.appUpdateInfo

        // Checking for Update
        task.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                askUserForUpdate()
            } else {
                makeIntent(Util.isUserLoggedIn())
            }
        }
    }

    private fun askUserForUpdate() {
        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = createBottomSheet()
        dialog.apply {

            optionsHeading.text = getString(R.string.wohoo_update_available)
            optionsContent.text =
                getString(R.string.new_update_description)
            positiveOption.text = getString(R.string.update)
            positiveOption.setTextColor(
                ContextCompat.getColor(
                    this@SplashActivity,
                    R.color.negative_red
                )
            )

            negativeOption.text = getString(R.string.skip_for_now)
            negativeOption.setTextColor(
                ContextCompat.getColor(
                  this@SplashActivity,
                    R.color.text_color
                )
            )

            positiveOption.setOnClickListener {
                bottomSheet.dismiss()
                val i = Intent(Intent.ACTION_VIEW)
                val uri = Uri.parse(Constants.APP_URL)
                i.data = uri
                startActivity(i)
                finish()
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
                makeIntent(Util.isUserLoggedIn())
            }
        }
        dialog.root.setBottomSheet(bottomSheet)
    }

    private fun makeIntent(userLoggedIn: Boolean) {
        val intent = if (userLoggedIn) {
            Intent(this@SplashActivity, MainActivity::class.java)
        } else {
            Intent(this@SplashActivity, SignInScreen::class.java)
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            startActivity(intent)
            finish()
        }
    }
}