package com.c22_ce02.awmonitorapp.data.repository

import com.c22_ce02.awmonitorapp.api.ApiService

class PostCurrentWeatherAndAirDataRepository(private val apiService: ApiService) {
    fun postCurrentWeatherAndAirData(
        location: String,
        date: String,
        aqi: Double,
        o3: Double,
        so2: Double,
        no2: Double,
        co: Double,
        pm10: Double,
        pm25: Double,
        humidity: Double,
        temperature: Double,
        windSpeed: Double
    ) = apiService.postCurrentWeatherAndAirData(
        location = location,
        date = date,
        aqi = aqi,
        o3 = o3,
        so2 = so2,
        no2 = no2,
        co = co,
        pm10 = pm10,
        pm25 = pm25,
        humidity = humidity,
        temperature = temperature,
        windSpeed = windSpeed,
    )
}