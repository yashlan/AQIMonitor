package com.c22_ce02.awmonitorapp.data.repository

import com.c22_ce02.awmonitorapp.api.ApiService

class AirQualityForecastAndHistoryByHourRepository(private val apiService: ApiService) {
    fun getAirQualityForecastAndHistoryByHour(
        lat: Double,
        lon: Double,
    ) = apiService.getAirQualityForecastAndHistoryByHour(
        lat,
        lon,
    )
}