package com.binay.shaw.justap.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.binay.shaw.justap.R

class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
}