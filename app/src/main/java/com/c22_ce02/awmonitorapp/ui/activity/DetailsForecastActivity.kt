package com.c22_ce02.awmonitorapp.ui.activity

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.data.model.AirQualityForecastByHour
import com.c22_ce02.awmonitorapp.databinding.ActivityDetailsForecastBinding
import com.c22_ce02.awmonitorapp.ui.fragment.HomeFragment
import com.c22_ce02.awmonitorapp.utils.Animation.startIncrementTextAnimation
import com.c22_ce02.awmonitorapp.utils.Text

class DetailsForecastActivity : AppCompatActivity(R.layout.activity_details_forecast) {

    private val binding by viewBinding(ActivityDetailsForecastBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getParcelableExtra<AirQualityForecastByHour>(HomeFragment.FORECAST_EXTRA)?.let {
            setupCustomActionBar(it.hour)
            with(binding) {
                root.setBackgroundResource(
                    when (it.aqi) {
                        in 0..50 -> R.drawable.window_bg_baik
                        in 51..100 -> R.drawable.window_bg_sedang
                        in 101..150 -> R.drawable.window_bg_tidak_sehat
                        in 151..300 -> R.drawable.window_bg_sangat_tidak_sehat
                        else -> R.drawable.window_bg_berbahaya
                    }
                )

                itemStatusAirMessage.tvAirStatusMsg.text = getAirStatusMessage(it.aqi)
                itemStatusAirMessage.root.setCardBackgroundColor(getItemStatusAirMessageBgColor(it.aqi))

                with(itemInfoAirForecast) {
                    startIncrementTextAnimation(it.aqi, tvAQI)
                    tvHumidity.text = "90%"
                    tvWindSpeed.text = "3.6 km/h"
                    tvTemperature.text = "23.4 C"
                    imgLabelAir.setBackgroundResource(
                        when (it.aqi) {
                            in 0..50 -> R.drawable.ic_label_detail_baik
                            in 51..100 -> R.drawable.ic_label_detail_sedang
                            in 101..150 -> R.drawable.ic_label_detail_tidak_sehat
                            in 151..300 -> R.drawable.ic_label_detail_sangat_tidak_sehat
                            else -> R.drawable.ic_label_detail_berbahaya
                        }
                    )
                }

                with(itemInfoListAirForecast) {

                    tvLabelPM10.text = Text.spannableStringBuilder(
                        getString(R.string.pm10),
                        '1',
                        0.7f
                    )
                    startIncrementTextAnimation(it.pm10, tvPm10)
                    iconStatusPM10.setImageResource(getIconItem(it.pm10))
                    tvStatusPM10.text = getStatusName(it.pm10)

                    tvLabelPM25.text = Text.spannableStringBuilder(
                        getString(R.string.pm25),
                        '2',
                        0.7f
                    )
                    startIncrementTextAnimation(it.pm25, tvPM25)
                    iconStatusPM25.setImageResource(getIconItem(it.pm25))
                    tvStatusPM25.text = getStatusName(it.pm25)

                    tvLabelSO2.text = Text.spannableStringBuilder(
                        getString(R.string.so2),
                        '2',
                        0.7f
                    )
                    startIncrementTextAnimation(it.so2, tvSO2)
                    iconStatusSO2.setImageResource(getIconItem(it.so2))
                    tvStatusSO2.text = getStatusName(it.so2)

                    tvLabelCO.text = getString(R.string.co)
                    startIncrementTextAnimation(it.co, tvCO)
                    iconStatusCO.setImageResource(getIconItem(it.co))
                    tvStatusCO.text = getStatusName(it.co)

                    tvLabelNO2.text = Text.spannableStringBuilder(
                        getString(R.string.no2),
                        '2',
                        0.7f
                    )
                    startIncrementTextAnimation(it.no2, tvNO2)
                    iconStatusNO2.setImageResource(getIconItem(it.no2))
                    tvStatusNO2.text = getStatusName(it.no2)

                    tvLabelO3.text = Text.spannableStringBuilder(
                        getString(R.string.o3),
                        '3',
                        0.7f
                    )
                    startIncrementTextAnimation(it.o3, tvO3)
                    iconStatusO3.setImageResource(getIconItem(it.o3))
                    tvStatusO3.text = getStatusName(it.o3)
                }
            }
        }
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
            initScrollViewListener()
        }
    }

    private fun initScrollViewListener() {
        binding.root.viewTreeObserver.addOnScrollChangedListener {
            if (binding.root.getChildAt(0).bottom
                <= (binding.root.height + binding.root.scrollY)
            ) {
                if (supportActionBar?.isShowing == true) {
                    supportActionBar?.hide()
                }
            } else {
                if (supportActionBar?.isShowing == false) {
                    supportActionBar?.show()
                }
            }
        }
    }

    private fun getIconItem(param: Int): Int {
        return when (param) {
            in 0..50 -> R.drawable.ic_air_quality_item_status_baik
            in 51..100 -> R.drawable.ic_air_quality_item_status_sedang
            in 101..150 -> R.drawable.ic_air_quality_item_status_tidak_sehat
            in 151..300 -> R.drawable.ic_air_quality_item_status_sangat_tidak_sehat
            else -> R.drawable.ic_air_quality_item_status_berbahaya
        }
    }

    private fun getStatusName(param: Int): String {
        return when (param) {
            in 0..50 -> getString(R.string.baik)
            in 51..100 -> getString(R.string.sedang)
            else -> getString(R.string.buruk)
        }
    }

    private fun getAirStatusMessage(aqi: Int): String {
        return when (aqi) {
            in 0..50 -> getString(R.string.status_air_forecast_baik_msg)
            in 51..100 -> getString(R.string.status_air_forecast_sedang_msg)
            in 101..150 -> getString(R.string.status_air_forecast_tidak_sehat_msg)
            in 151..300 -> getString(R.string.status_air_forecast_sangat_tidak_sehat_msg)
            else -> getString(R.string.status_air_forecast_berbahaya_msg)
        }
    }

    private fun getItemStatusAirMessageBgColor(aqi: Int): Int {
        return ContextCompat.getColor(
            this,
            when (aqi) {
                in 0..100 -> R.color.warna_baik
                else -> R.color.deep_orange
            }
        )
    }
}