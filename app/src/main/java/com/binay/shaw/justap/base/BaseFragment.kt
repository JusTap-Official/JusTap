package com.binay.shaw.justap.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.binay.shaw.justap.R
import com.tapadoo.alerter.Alerter


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


    private fun showNoInternetDialog() {
        Alerter.create(requireActivity())
            .setTitle(getString(R.string.noInternet))
            .setText(getString(R.string.noInternetDescription))
            .setBackgroundColorInt(ResourcesCompat.getColor(resources, R.color.negative, null))
            .setIcon(R.drawable.wifi_off)
            .setDuration(2000L)
            .show()
    }

    fun showAlerter(title: String, message: String, color: Int, drawable: Int, timing: Long) {
        Alerter.create(requireActivity())
            .setTitle(title)
            .setText(message)
            .setBackgroundColorInt(color)
            .setIcon(drawable)
            .setDuration(timing)
            .show()
    }

    private fun hideProgress() {
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

    fun showKeyboard(mEtSearch: EditText, context: Context) {
        mEtSearch.requestFocus()
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mEtSearch, 0)
    }

    fun hideKeyboard() {
        val view: View? = requireActivity().currentFocus
        view?.let {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}