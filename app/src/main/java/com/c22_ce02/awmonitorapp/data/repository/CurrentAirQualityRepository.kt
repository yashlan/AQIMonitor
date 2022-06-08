package com.c22_ce02.awmonitorapp.data.repository

import com.c22_ce02.awmonitorapp.api.ApiService

class CurrentAirQualityRepository(private val apiService: ApiService) {
    fun getCurrentAirQuality(
        lat: Double,
        lon: Double,
    ) =
        apiService.getCurrentAirQuality(
            lat,
            lon,
        )
}