package com.c22_ce02.awmonitorapp.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.ui.view.model.PostCurrentWeatherAndAirDataViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.PostCurrentWeatherAndAirDataViewModelFactory

class PostData(private val fragment: Fragment) {

    private val postCurrentWeatherAndAirDataViewModel: PostCurrentWeatherAndAirDataViewModel by fragment.viewModels {
        PostCurrentWeatherAndAirDataViewModelFactory()
    }

    fun postCurrentWeatherAndAirData(
        location: String,
        date: String,
        aqi: Double,
        o3: Double,
        so2: Double,
        no2: Double,
        co: Double,
        pm10: Double,
        pm25: Double,
        humidity: Double,
        temperature: Double,
        windSpeed: Double
    ) {
        postCurrentWeatherAndAirDataViewModel.postCurrentWeatherAndAirData(
            location = location,
            date = date,
            aqi = aqi,
            o3 = o3,
            so2 = so2,
            no2 = no2,
            co = co,
            pm10 = pm10,
            pm25 = pm25,
            humidity = humidity,
            temperature = temperature,
            windSpeed = windSpeed,
            onSuccess = { successMsg ->
                if (successMsg != null && BuildConfig.DEBUG) {
                    fragment.requireContext().showToast(successMsg.toString())
                }
            },
            onError = { errorMsg ->
                if (errorMsg != null && BuildConfig.DEBUG) {
                    fragment.requireContext().showToast(errorMsg.toString())
                }
            }
        )
    }
}
