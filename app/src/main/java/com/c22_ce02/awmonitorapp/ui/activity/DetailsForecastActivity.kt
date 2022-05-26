package com.c22_ce02.awmonitorapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.ActivityDetailsForecastBinding
import com.c22_ce02.awmonitorapp.utils.setFullscreen
import com.c22_ce02.awmonitorapp.utils.viewBinding

class DetailsForecastActivity : AppCompatActivity(R.layout.activity_details_forecast) {

    private val binding by viewBinding(ActivityDetailsForecastBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCustomActionBar("12am")

        binding.root.setBackgroundResource(R.drawable.window_bg_sangat_tidak_sehat)
        binding.itemInfoAirForecast.tvAQI.text = "122"
        binding.itemInfoAirForecast.tvHumidity.text = "90%"
        binding.itemInfoAirForecast.tvWindSpeed.text = "3.6 km/h"
        binding.itemInfoAirForecast.tvTemperature.text = "23.4 C"
        binding.itemInfoAirForecast.imgLabelAir.setBackgroundResource(
            R.drawable.ic_label_detail_sangat_tidak_sehat
        )
    }

    private fun setupCustomActionBar(title: String) {
        with(supportActionBar) {
            this?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            this?.setCustomView(R.layout.action_bar_detail_forecast)

            val tvHour = this?.customView?.findViewById<TextView>(R.id.tvHour)
            tvHour?.text = title

            val btnBack = this?.customView?.findViewById<Button>(R.id.btnBack)
            btnBack?.setOnClickListener {
                btnBack.startAnimation(AlphaAnimation(1f, 0.5f))
                finish()
            }
        }
    }
}