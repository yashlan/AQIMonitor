package com.c22_ce02.awmonitorapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AirQualityAndWeatherHistoryForecastByHour(
    val historyAndForecastAirQuality: AirQualityHistoryAndForecastByHour,
    val forecastWeather: WeatherForecastByHour
) : Parcelable