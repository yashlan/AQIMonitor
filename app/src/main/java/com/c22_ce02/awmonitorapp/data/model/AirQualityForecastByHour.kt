package com.c22_ce02.awmonitorapp.data.model


data class AirQualityForecastByHour(
    val hour: String,
    val iconAQISrc: Int,
    val aqi: Int
)