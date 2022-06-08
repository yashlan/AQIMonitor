package com.c22_ce02.awmonitorapp.data.repository

import com.c22_ce02.awmonitorapp.api.ApiService

class WeatherForecastByHourRepository(private val apiService: ApiService) {
    fun getWeatherForecastByHour(
        lat: Double,
        lon: Double,
        hours: Int
    ) = apiService.getWeatherForecastByHour(
        lat,
        lon,
        hours
    )
}