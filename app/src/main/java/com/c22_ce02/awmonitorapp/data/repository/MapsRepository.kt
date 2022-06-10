package com.c22_ce02.awmonitorapp.data.repository

import com.c22_ce02.awmonitorapp.api.ApiService

class MapsRepository(private val apiService: ApiService) {
    fun getCurrentAirQuality34Province() = apiService.getCurrentAirQuality34Province()
}