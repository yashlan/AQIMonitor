package com.c22_ce02.awmonitorapp.ui.view.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.response.AirQualityForecastAndHistoryByHourResponse
import com.c22_ce02.awmonitorapp.data.repository.AirQualityForecastAndHistoryByHourRepository
import retrofit2.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class AirQualityForecastAndHistoryByHourViewModel : ViewModel() {

    private val amPm = SimpleDateFormat("a", Locale("id")).format(Date()).lowercase()
    private val hour = SimpleDateFormat("hh", Locale("id")).format(Date()).toInt()

    fun getAirQualityForecastAndHistoryByHour(
        onSuccess: (AirQualityForecastAndHistoryByHourResponse.Data?) -> Unit,
    ) {
        val data = AirQualityForecastAndHistoryByHourResponse.Data(
            listOf(
                AirQualityForecastAndHistoryByHourResponse.History(
                    aqi = Random.nextDouble(1.0, 350.0),
                    o3 = 89.2,
                    so2 = 2.8594,
                    no2 = 5.42472,
                    co = 343.9,
                    pm10 = 232.5,
                    pm25 = 353.4,
                    datetime = hour.minus(3).toString() + amPm
                ),
                AirQualityForecastAndHistoryByHourResponse.History(
                    aqi = Random.nextDouble(1.0, 350.0),
                    o3 = 89.2,
                    so2 = 2.8594,
                    no2 = 5.42472,
                    co = 343.9,
                    pm10 = 232.5,
                    pm25 = 353.4,
                    datetime = hour.minus(2).toString() + amPm
                ),
                AirQualityForecastAndHistoryByHourResponse.History(
                    aqi = Random.nextDouble(1.0, 350.0),
                    o3 = 89.2,
                    so2 = 2.8594,
                    no2 = 5.42472,
                    co = 343.9,
                    pm10 = 232.5,
                    pm25 = 353.4,
                    datetime = hour.minus(1).toString() + amPm
                )
            ),
            listOf(
                AirQualityForecastAndHistoryByHourResponse.Forecast(
                    aqi = Random.nextDouble(1.0, 350.0),
                    o3 = 89.2,
                    so2 = 2.8594,
                    no2 = 5.42472,
                    co = 343.9,
                    pm10 = 232.5,
                    pm25 = 353.4,
                    datetime = hour.plus(1).toString() + amPm
                ),
                AirQualityForecastAndHistoryByHourResponse.Forecast(
                    aqi = Random.nextDouble(1.0, 350.0),
                    o3 = 89.2,
                    so2 = 2.8594,
                    no2 = 5.42472,
                    co = 343.9,
                    pm10 = 232.5,
                    pm25 = 353.4,
                    datetime = hour.plus(2).toString() + amPm
                ),
                AirQualityForecastAndHistoryByHourResponse.Forecast(
                    aqi = Random.nextDouble(1.0, 350.0),
                    o3 = 89.2,
                    so2 = 2.8594,
                    no2 = 5.42472,
                    co = 343.9,
                    pm10 = 232.5,
                    pm25 = 353.4,
                    datetime = hour.plus(3).toString() + amPm
                )
            )
        )
        onSuccess.invoke(data)
    }
}