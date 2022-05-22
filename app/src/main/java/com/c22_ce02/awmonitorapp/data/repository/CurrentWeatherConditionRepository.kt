package com.c22_ce02.awmonitorapp.data.repository

import com.c22_ce02.awmonitorapp.api.ApiService

class CurrentWeatherConditionRepository(private val apiService: ApiService) {
    fun getCurrentWeatherCondition(
        lat: Double,
        lon: Double,
        apiKey: String
    ) = apiService.getCurrentWeatherCondition(
        lat,
        lon,
        apiKey
    )
}