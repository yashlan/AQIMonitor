package com.c22_ce02.awmonitorapp.data.repository

import com.c22_ce02.awmonitorapp.api.ApiService

class AirQualityForecastByHourRepository(private val apiService: ApiService) {
    fun getAirQualityForecastByHour(
        lat: Double,
        lon: Double,
        apiKey: String,
        hours: Int
    ) = apiService.getAirQualityForecastByHour(
        lat,
        lon,
        apiKey,
        hours
    )
}