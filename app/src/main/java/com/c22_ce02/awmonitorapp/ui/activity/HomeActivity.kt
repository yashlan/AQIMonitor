package com.c22_ce02.awmonitorapp.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.ActivityHomeBinding
import com.c22_ce02.awmonitorapp.notification.AirQualityNotificationReceiver
import com.c22_ce02.awmonitorapp.utils.*


class HomeActivity : AppCompatActivity(R.layout.activity_home) {

    private var canExit = false
    private lateinit var navController: NavController
    private val binding by viewBinding(ActivityHomeBinding::bind, R.id.container)

    private val backgroundLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when {
                    isGranted -> {
                        return@registerForActivityResult
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION) -> {
                        return@registerForActivityResult
                    }
                    else -> {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        forcePortraitScreenOrientation()
        setFullscreen()
        super.onCreate(savedInstanceState)
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragmentActivityHomeActivity.id)
        navHostFragment?.findNavController()?.let {
            navController = it
        }
        val appBarConfiguration = AppBarConfiguration.Builder(
            setOf(
                R.id.navigation_home,
                R.id.navigation_maps,
                R.id.navigation_article,
                R.id.navigation_profile,
            )
        ).build()
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        removeItemTintColor()
        setupNotification()

        if (isAllLocationGranted()) {
            askIgnoreBatteryOptimization()
            askAccessBackgroundLocation()
        }

    }

    private fun isAllLocationGranted(): Boolean =
        isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) &&
                isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)

    private fun askAccessBackgroundLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!isPermissionGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                createCustomAlertDialog(
                    getString(R.string.konfirmasi),
                    getString(R.string.permission_msg_access_bg_location),
                    actionPositiveButton = {
                        backgroundLocationPermission.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    }
                )
            }
        }
    }

    @SuppressLint("BatteryLife")
    private fun askIgnoreBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.flags = FLAG_ACTIVITY_NEW_TASK
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }

    private fun setupNotification() {
        val receiver = AirQualityNotificationReceiver()
        receiver.createChannel(this)
        receiver.setRepeatingNotification(this)
    }

    private fun removeItemTintColor() {
        binding.navView.itemIconTintList = null
    }

    override fun onBackPressed() {
        if (canExit) {
            super.onBackPressed()
            finishAffinity()
            return
        }
        canExit = true
        showToast(R.string.exit_info)
        Handler(Looper.getMainLooper()).postDelayed({
            canExit = false
        }, DELAY_EXIT)
    }

    fun onClickItemNavHome(item: MenuItem) {
        if (navController.currentDestination?.id == R.id.navigation_home) {
            return
        }
        navController.navigate(R.id.navigation_home)
        changeStateIconItem(item)
    }

    fun onClickItemNavDiscover(item: MenuItem) {
        if (navController.currentDestination?.id == R.id.navigation_maps) {
            return
        }
        navController.navigate(R.id.navigation_maps)
        changeStateIconItem(item)
    }

    fun onClickItemNavArticle(item: MenuItem) {
        if (navController.currentDestination?.id == R.id.navigation_article) {
            return
        }
        navController.navigate(R.id.navigation_article)
        changeStateIconItem(item)
    }

    fun onClickItemNavProfile(item: MenuItem) {
        if (navController.currentDestination?.id == R.id.navigation_profile) {
            return
        }
        navController.navigate(R.id.navigation_profile)
        changeStateIconItem(item)
    }

    private fun changeStateIconItem(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.item_navigation_home -> {
                menuItem.isChecked = true
            }
            R.id.item_navigation_discover -> {
                menuItem.isChecked = true
            }
            R.id.item_navigation_article -> {
                menuItem.isChecked = true
            }
            R.id.item_navigation_profile -> {
                menuItem.isChecked = true
            }
        }
    }

    companion object {
        private const val DELAY_EXIT: Long = 2000
    }
}