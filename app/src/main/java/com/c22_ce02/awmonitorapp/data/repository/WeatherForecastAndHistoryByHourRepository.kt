package com.c22_ce02.awmonitorapp.data.repository

import com.c22_ce02.awmonitorapp.api.ApiService

class WeatherForecastAndHistoryByHourRepository(private val apiService: ApiService) {
    fun getWeatherForecastAndHistoryByHour(
        lat: Double,
        lon: Double
    ) = apiService.getWeatherForecastAndHistoryByHour(
        lat,
        lon
    )
}