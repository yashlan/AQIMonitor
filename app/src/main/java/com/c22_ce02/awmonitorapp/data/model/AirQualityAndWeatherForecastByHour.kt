package com.c22_ce02.awmonitorapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AirQualityAndWeatherForecastByHour(
    val forecastAirQuality: AirQualityForecastByHour,
    val forecastWeather: WeatherForecastByHour
) : Parcelable