package com.c22_ce02.awmonitorapp.data.repository

import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.api.ApiService

class CurrentConditionRepository(private val apiService: ApiService) {
    fun getCurrentCondition(locationKey: String) = apiService.getCurrentCondition(
        "${BuildConfig.BASE_URL_ACCUWEATHER}currentconditions/v1/${locationKey}?apikey=${BuildConfig.API_KEY_ACCUWEATHER}&language=id"
    )
}