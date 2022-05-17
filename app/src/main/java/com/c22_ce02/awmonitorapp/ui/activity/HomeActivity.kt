package com.c22_ce02.awmonitorapp.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.databinding.ActivityHomeBinding
import com.c22_ce02.awmonitorapp.ui.view.model.CurrentAirQualityViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.CurrentConditionViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.FiveDaysOfDailyForecastViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.GeoPositionViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.CurrentAirQualityViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.CurrentConditionViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.FiveDaysOfDailyForecastViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.GeoPositionViewModelFactory
import com.c22_ce02.awmonitorapp.utils.showToast
import com.c22_ce02.awmonitorapp.utils.viewBinding

class HomeActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityHomeBinding>()
    private val geoPositionViewModel: GeoPositionViewModel by viewModels {
        GeoPositionViewModelFactory()
    }
    private val fiveDaysOfDailyForecastViewModel: FiveDaysOfDailyForecastViewModel by viewModels {
        FiveDaysOfDailyForecastViewModelFactory()
    }
    private val currentConditionViewModel: CurrentConditionViewModel by viewModels {
        CurrentConditionViewModelFactory()
    }
    private val currentAirQualityViewModel: CurrentAirQualityViewModel by viewModels {
        CurrentAirQualityViewModelFactory()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentAirQualityViewModel.getCurrentAirQuality(
            -5.299,
            104.98,
            BuildConfig.API_KEY_WEATHERBIT,
            onSuccess = {
                showToast(it?.data?.size.toString())
                val data = it?.data?.get(0)
                binding.tvAirQualityToday.text =
                    "AQI = ${data?.aqi}\n" +
                            "O3 = ${data?.o3}\n" +
                            "SO2 = ${data?.so2}\n" +
                            "NO2 = ${data?.no2}\n" +
                            "CO = ${data?.co}\n" +
                            "PM10 = ${data?.pm10}\n" +
                            "PM25 = ${data?.pm25}\n"
            },
            onFailed = { errorMsg ->
                if (errorMsg != null) {
                    showToast(errorMsg)
                }
            }
        )

        geoPositionViewModel.getInformationUserByGeoPosition(
            "-5.299,104.98",
            onSuccess = { r ->
                binding.tvMyLocation.text =
                    "Lokasi Saya : ${r?.localizedName}, " +
                            "${r?.administrativeArea?.localizedName}, " +
                            "${r?.country?.localizedName}"

                if (r?.locationKey != null) {
                    currentConditionViewModel.getCurrentCondition(
                        r.locationKey,
                        onSuccess = {
                            val tempC =
                                "${it?.get(0)?.temperature?.metric?.value}${it?.get(0)?.temperature?.metric?.unit}"
                            binding.tvCurrentCondition.text =
                                "Cuaca Saat Ini : ${it?.get(0)?.weatherText}, $tempC"
                        },
                        onFailed = { errorMsg ->
                            if (errorMsg != null) {
                                showToast("currentConditionError : $errorMsg")
                            }
                        }
                    )

                    fiveDaysOfDailyForecastViewModel.get5DaysOfDailyForecasts(
                        r.locationKey,
                        onSuccess = {
                            val f0 =
                                "${it?.dailyForecasts?.get(0)?.temperature?.minimum?.value}F - ${
                                    it?.dailyForecasts?.get(0)?.temperature?.maximum?.value
                                }F"
                            val f1 =
                                "${it?.dailyForecasts?.get(1)?.temperature?.minimum?.value}F - ${
                                    it?.dailyForecasts?.get(1)?.temperature?.maximum?.value
                                }F"
                            val f2 =
                                "${it?.dailyForecasts?.get(2)?.temperature?.minimum?.value}F - ${
                                    it?.dailyForecasts?.get(2)?.temperature?.maximum?.value
                                }F"
                            val f3 =
                                "${it?.dailyForecasts?.get(3)?.temperature?.minimum?.value}F - ${
                                    it?.dailyForecasts?.get(3)?.temperature?.maximum?.value
                                }F"
                            val f4 =
                                "${it?.dailyForecasts?.get(4)?.temperature?.minimum?.value}F - ${
                                    it?.dailyForecasts?.get(4)?.temperature?.maximum?.value
                                }F"

                            with(binding) {
                                tvForecastDay1.text = "Perkiraan Suhu : $f0"
                                tvForecastDay2.text = "Perkiraan Suhu : $f1"
                                tvForecastDay3.text = "Perkiraan Suhu : $f2"
                                tvForecastDay4.text = "Perkiraan Suhu : $f3"
                                tvForecastDay5.text = "Perkiraan Suhu : $f4"
                            }
                        },
                        onFailed = { errorMsg ->
                            if (errorMsg != null) {
                                showToast(errorMsg)
                            }
                        }
                    )
                }
            },
            onFailed = { errorMsg ->
                if (errorMsg != null) {
                    showToast(errorMsg)
                }
            })
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}