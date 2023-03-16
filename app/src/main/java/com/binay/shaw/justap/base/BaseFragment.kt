package com.binay.shaw.justap.base

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tapadoo.alerter.Alerter
import java.util.*


abstract class BaseFragment : Fragment() {
    private lateinit var progressDialogue: Dialog

    override fun onPause() {
        hideProgress()
        super.onPause()
    }

    /**
     * Generic function to get parent activity of the fragment. Since the function is inlined, no reflection is needed and normal operators like !is and as are now available for you to use
     */
    inline fun <reified T : AppCompatActivity> getParentActivity(): T? {
        var parentActivity: T? = null
        activity?.let {
            parentActivity = it as T
        }
        return parentActivity
    }

//    protected open fun observeProgress(viewModel: BaseViewModel, isDismissible: Boolean = true) {
//        viewModel.progressLiveData.observe(this) { progress ->
//            if (progress) {
//                showProgress(isDismissible)
//                Log.d("saurabh", "show progress $javaClass")
//            } else {
//                hideProgress()
//            }
//        }
//    }

//    protected open fun obServeErrorAndException(apiError: ApiError, viewModel: BaseViewModel) {
//        (activity as BaseActivity).showErrorDialog(null, apiError.message)
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

//    protected open fun showErrorDialog(
//        header: String? = null,
//        message: String? = null,
//    ) {
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setTitle(if (header.isNullOrEmpty()) getString(R.string.error) else header)
//        builder.setMessage(if (message.isNullOrEmpty()) getString(R.string.some_error_occoured) else message)
//        builder.setPositiveButton(R.string.ok) { dialogInterface, _ ->
//            dialogInterface.dismiss()
//        }
//        builder.setOnDismissListener {
//        }
//        builder.setCancelable(false)
//        val dialog = builder.create()
//        dialog.show()
//    }

    /* Kotlin requires explicit modifiers for overridable members and overrides. Add open if you need function/member to be overridable by default they are final.
        public, protected, internal and private are visibility modifiers, by default public
     */
//    protected fun showProgress(isDismissible: Boolean) {
//        if (this::progressDialogue.isInitialized.not()) {
//            progressDialogue =
//                RelativeLayoutProgressDialog.onCreateDialogModel(requireActivity()).apply {
//                    setCancelable(isDismissible)
//                }
//        }
//        progressDialogue.show()
//    }

    fun showAlerter(title: String, message: String, color: Int, drawable: Int, timing: Long) {
        Alerter.create(requireActivity())
            .setTitle(title)
            .setText(message)
            .setBackgroundColorInt(color)
            .setIcon(drawable)
            .setDuration(timing)
            .show()
    }

    protected fun hideProgress() {
        if (this::progressDialogue.isInitialized && progressDialogue.isShowing) {
            progressDialogue.dismiss()
            Log.d("saurabh", "hide progress $javaClass")
        }
    }

    fun showKeyboard(editText: EditText) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }

    fun hideKeyboard() {
        val view: View? = requireActivity().currentFocus
        view?.let {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

//    abstract fun getScreenName(): String

    /**
     * Return App preference being set and used throughout the app. An replacement of {com.zinka.fleet.dataBase.StoredObjectValue}
     *
     * @return [AppPreference]
     */
//    fun getAppPreference(): AppPreference {
//        return (requireContext().applicationContext as NagarApp).appSharedPreference
//    }

    /**
     * Return User preference data(i.e user profile) being set and used throughout the app. An replacement of {com.zinka.fleet.dataBase.StoredObjectValue}
     *
     * @return [UserPreference]
     */
//    fun getUserPreference(): UserPreference {
//        return (requireContext().applicationContext as NagarApp).userSharedPreference
//    }
}