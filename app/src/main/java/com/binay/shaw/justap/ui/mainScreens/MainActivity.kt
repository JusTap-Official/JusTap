package com.binay.shaw.justap.ui.mainScreens


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseActivity
import com.binay.shaw.justap.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : BaseActivity() {

    private var timer = 0L
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        getToken()
        setUpNav()
    }

    private fun setUpNav() {
        val bottomNavigationView = binding.bottomNav
        val navController: NavController = findNavController(R.id.fragmentContainerView)
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.home, R.id.scanner, R.id.history, R.id.settings))
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.home -> showBottomNav(bottomNavigationView)
                R.id.scanner -> showBottomNav(bottomNavigationView)
                R.id.history -> showBottomNav(bottomNavigationView)
                R.id.settings -> showBottomNav(bottomNavigationView)
                else -> hideBottomNav(bottomNavigationView)
            }
        }

        bottomNavigationView.setupWithNavController2(navController)
    }

//    private fun getToken() {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("Token Error", "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//            val token = task.result
//            Log.d("Token", token)
//        })
//    }

    private fun showBottomNav(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNav(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.visibility = View.GONE
    }

    private fun BottomNavigationView.setupWithNavController2(navController: NavController) {
        val bottomNavigationView = this
        bottomNavigationView.setOnItemReselectedListener { item ->
            val reselectedDestinationId = item.itemId
            navController.popBackStack(reselectedDestinationId, false)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        val navController: NavController = findNavController(R.id.fragmentContainerView)
        val count = navController.backQueue.size

        if (count <= 2) {
            if (timer + 2000L > System.currentTimeMillis()) {
                onBackPressedDispatcher.onBackPressed()
            } else {
                Toast.makeText(
                    applicationContext, getString(R.string.press_once_again_to_exit),
                    Toast.LENGTH_SHORT
                ).show()
            }
            timer = System.currentTimeMillis()
        } else {
            navController.popBackStack()
        }
    }
}