package com.c22_ce02.awmonitorapp.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.data.model.AirQualityAndWeatherHistoryForecastByHour
import com.c22_ce02.awmonitorapp.databinding.ActivityDetailsForecastBinding
import com.c22_ce02.awmonitorapp.ui.fragment.HomeFragment
import com.c22_ce02.awmonitorapp.utils.forcePortraitScreenOrientation
import com.c22_ce02.awmonitorapp.utils.spannableStringBuilder
import com.c22_ce02.awmonitorapp.utils.startIncrementTextAnimation
import java.text.SimpleDateFormat
import java.util.*

class DetailsForecastActivity : AppCompatActivity(R.layout.activity_details_forecast) {

    private val binding by viewBinding(ActivityDetailsForecastBinding::bind)
    private val currentHour = SimpleDateFormat("ha", Locale("id")).format(Date()).lowercase()

    override fun onCreate(savedInstanceState: Bundle?) {
        forcePortraitScreenOrientation()
        super.onCreate(savedInstanceState)

        intent.getParcelableExtra<AirQualityAndWeatherHistoryForecastByHour>(HomeFragment.FORECAST_EXTRA)
            ?.let {
                if (currentHour.equals(it.historyAndForecastAirQuality.hour, true)) {
                    setupCustomActionBar(getString(R.string.now))
                } else {
                    setupCustomActionBar(it.historyAndForecastAirQuality.hour)
                }
                with(binding) {
                    root.setBackgroundResource(
                        when (it.historyAndForecastAirQuality.aqi) {
                            in 0..50 -> R.drawable.window_bg_baik
                            in 51..100 -> R.drawable.window_bg_sedang
                            in 101..150 -> R.drawable.window_bg_tidak_sehat
                            in 151..300 -> R.drawable.window_bg_sangat_tidak_sehat
                            else -> R.drawable.window_bg_berbahaya
                        }
                    )

                    itemStatusAirMessage.tvAirStatusMsg.text =
                        getAirStatusMessage(it.historyAndForecastAirQuality.aqi)
                    itemStatusAirMessage.root.setCardBackgroundColor(
                        getItemStatusAirMessageBgColor(
                            it.historyAndForecastAirQuality.aqi
                        )
                    )

                    with(itemInfoAirForecast) {
                        startIncrementTextAnimation(it.historyAndForecastAirQuality.aqi, tvAQI)
                        startIncrementTextAnimation(
                            convertWindSpeedToKmh(it.forecastWeather.windSpeed),
                            " km/h",
                            tvWindSpeed
                        )
                        startIncrementTextAnimation(
                            it.forecastWeather.temperature,
                            " C",
                            tvTemperature
                        )
                        startIncrementTextAnimation(
                            it.forecastWeather.humidity,
                            "%",
                            tvHumidity
                        )

                        imgLabelAir.setBackgroundResource(
                            when (it.historyAndForecastAirQuality.aqi) {
                                in 0..50 -> R.drawable.ic_label_detail_baik
                                in 51..100 -> R.drawable.ic_label_detail_sedang
                                in 101..150 -> R.drawable.ic_label_detail_tidak_sehat
                                in 151..300 -> R.drawable.ic_label_detail_sangat_tidak_sehat
                                else -> R.drawable.ic_label_detail_berbahaya
                            }
                        )
                    }

                    with(itemInfoListAirForecast) {

                        tvLabelPM10.text = spannableStringBuilder(
                            getString(R.string.pm10),
                            '1',
                            0.7f
                        )
                        startIncrementTextAnimation(it.historyAndForecastAirQuality.pm10, tvPm10)
                        iconStatusPM10.setImageResource(getIconItem(it.historyAndForecastAirQuality.pm10))
                        tvStatusPM10.text = getStatusName(it.historyAndForecastAirQuality.pm10)

                        tvLabelPM25.text = spannableStringBuilder(
                            getString(R.string.pm25),
                            '2',
                            0.7f
                        )
                        startIncrementTextAnimation(it.historyAndForecastAirQuality.pm25, tvPM25)
                        iconStatusPM25.setImageResource(getIconItem(it.historyAndForecastAirQuality.pm25))
                        tvStatusPM25.text = getStatusName(it.historyAndForecastAirQuality.pm25)

                        tvLabelSO2.text = spannableStringBuilder(
                            getString(R.string.so2),
                            '2',
                            0.7f
                        )
                        startIncrementTextAnimation(it.historyAndForecastAirQuality.so2, tvSO2)
                        iconStatusSO2.setImageResource(getIconItem(it.historyAndForecastAirQuality.so2))
                        tvStatusSO2.text = getStatusName(it.historyAndForecastAirQuality.so2)

                        tvLabelCO.text = getString(R.string.co)
                        startIncrementTextAnimation(it.historyAndForecastAirQuality.co, tvCO)
                        iconStatusCO.setImageResource(getIconItem(it.historyAndForecastAirQuality.co))
                        tvStatusCO.text = getStatusName(it.historyAndForecastAirQuality.co)

                        tvLabelNO2.text = spannableStringBuilder(
                            getString(R.string.no2),
                            '2',
                            0.7f
                        )
                        startIncrementTextAnimation(it.historyAndForecastAirQuality.no2, tvNO2)
                        iconStatusNO2.setImageResource(getIconItem(it.historyAndForecastAirQuality.no2))
                        tvStatusNO2.text = getStatusName(it.historyAndForecastAirQuality.no2)

                        tvLabelO3.text = spannableStringBuilder(
                            getString(R.string.o3),
                            '3',
                            0.7f
                        )
                        startIncrementTextAnimation(it.historyAndForecastAirQuality.o3, tvO3)
                        iconStatusO3.setImageResource(getIconItem(it.historyAndForecastAirQuality.o3))
                        tvStatusO3.text = getStatusName(it.historyAndForecastAirQuality.o3)
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

    private fun convertWindSpeedToKmh(speedMs: Int): Int = (speedMs * 3.6).toInt()

}