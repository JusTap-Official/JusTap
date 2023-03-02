package com.binay.shaw.justap.base

import android.app.Dialog
import android.app.LocaleManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import java.io.IOException

/**
 * Created by binay on 02,March,2023
 */
abstract class BaseActivity : AppCompatActivity() {
    private lateinit var progressDialogue: Dialog

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        changeStatusBarColor(Color.TRANSPARENT)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

//        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
    }

    override fun onStart() {
        super.onStart()
        supportActionBar?.hide()
    }

//    override fun attachBaseContext(newBase: Context) {
//        super.attachBaseContext(LocaleManager.getWrapper(newBase))
//    }

    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
//        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
    }

    override fun onDestroy() {
        super.onDestroy()
        hideKeyboard()
        hideProgress()
    }

    protected open fun observeProgress(viewModel: BaseViewModel, isDismissible: Boolean = true) {
        viewModel.progressLiveData.observe(this) { progress ->
            if (progress) {
//                showProgress(isDismissible)
            } else {
                hideProgress()
            }
        }
    }

//    protected open fun obServeErrorAndException(apiError: ApiError, viewModel: BaseViewModel) {
//        showErrorDialog(null, apiError.message)
//        observerException(viewModel)
//    }

//    protected open fun observeErrorAndException(viewModel: BaseViewModel) {
//        viewModel.errorLiveData.observe(this) {
//            showErrorDialog(null, it.message)
//        }
//        observerException(viewModel)
//    }

//    protected open fun observerException(viewModel: BaseViewModel) {
//        viewModel.exceptionLiveData.observe(this) { exception ->
//            if (exception is IOException) {
//                showNoInternetDialog()
//            } else {
//                showErrorDialog()
//            }
//        }
//    }

//    private fun showNoInternetDialog() {
//        showErrorDialog(getString(R.string.no_internet), getString(R.string.no_internet_message))
//    }

//    fun showErrorDialog(
//        header: String? = null,
//        message: String? = null,
//    ) {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle(if (header.isNullOrEmpty()) getString(R.string.error) else header)
//        builder.setMessage(if (message.isNullOrEmpty()) getString(R.string.some_error_occoured) else message)
//        builder.setPositiveButton(R.string.ok) { dialogInterface, _ ->
//            dialogInterface.dismiss()
//        }
//        builder.setOnDismissListener {
//            finish()
//        }
//        builder.setCancelable(false)
//        val dialog = builder.create()
//        dialog.show()
//    }

//    protected fun showProgress(isDismissible: Boolean) {
//        if (this::progressDialogue.isInitialized.not()) {
//            progressDialogue = RelativeLayoutProgressDialog.onCreateDialogModel(this).apply {
//                setCancelable(isDismissible)
//            }
//        }
//        if (progressDialogue.isShowing.not()) {
//            progressDialogue.show()
//        }
//    }

    protected fun hideProgress() {
        if (this::progressDialogue.isInitialized && progressDialogue.isShowing) {
            progressDialogue.hide()
        }
    }

    fun hideKeyboard() {
        val view: View? = this.currentFocus
        view?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    /**
     * Sets the Status Bar Color
     * @param color, is the id value of the color resource
     */
    protected fun changeStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = true
            }
            window.statusBarColor = ContextCompat.getColor(this, color)
        }
    }

//    abstract fun getScreenName(): String

    /**
     * Return App preference being set and used throughout the app
     * @return [AppPreference]
     */
//    fun getAppPreference(): AppPreference {
//        return (application as NagarApp).appSharedPreference
//    }

    /**
     * Return User preference data(i.e user profile) being set and used throughout the app.
     * @return [UserPreference]
     */
//    fun getUserPreference(): UserPreference {
//        return (application as NagarApp).userSharedPreference
//    }
}