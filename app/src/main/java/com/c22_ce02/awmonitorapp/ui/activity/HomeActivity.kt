package com.c22_ce02.awmonitorapp.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.ActivityHomeBinding
import com.c22_ce02.awmonitorapp.utils.setFullscreen
import com.c22_ce02.awmonitorapp.utils.showToast


class HomeActivity : AppCompatActivity(R.layout.activity_home) {

    private var canExit = false
    private lateinit var navController: NavController
    private val binding by viewBinding(ActivityHomeBinding::bind, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
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
                R.id.mapsFragment,
                R.id.navigation_glossary,
            )
        ).build()
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        removeItemTintColor()

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
        }, 2000)
    }

    fun onClickItemNavHome(item: MenuItem) {
        if (navController.currentDestination?.id == R.id.navigation_home) {
            return
        }
        navController.navigate(R.id.navigation_home)
        changeStateIconItem(item)
    }

    fun onClickItemNavDiscover(item: MenuItem) {
        if (navController.currentDestination?.id == R.id.mapsFragment) {
            return
        }
        navController.navigate(R.id.mapsFragment)
        changeStateIconItem(item)
    }

    fun onClickItemNavGlossary(item: MenuItem) {
        if (navController.currentDestination?.id == R.id.navigation_glossary) {
            return
        }
        navController.navigate(R.id.navigation_glossary)
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
            R.id.item_navigation_glossary -> {
                menuItem.isChecked = true
            }
        }
    }
}