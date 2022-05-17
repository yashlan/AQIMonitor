package com.c22_ce02.awmonitorapp.data.repository

import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.api.ApiService

class FiveDaysOfDailyForecastRepository(private val apiService: ApiService) {
    fun get5DaysOfDailyForecasts(locationKey: String) =
        apiService.get5DaysOfDailyForecasts(
            "${BuildConfig.BASE_URL_ACCUWEATHER}forecasts/v1/daily/5day/${locationKey}?apikey=${BuildConfig.API_KEY_ACCUWEATHER}&language=id"
        )
}