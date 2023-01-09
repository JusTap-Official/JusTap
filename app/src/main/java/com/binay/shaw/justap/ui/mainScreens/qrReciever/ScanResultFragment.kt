package com.binay.shaw.justap.ui.mainScreens.qrReciever

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.Util

class ResultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val string = arguments?.getString("decryptedString")
        Util.log("Data is: $string")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_result, container, false)
    }

    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                true
            } else false
        }
    }

}