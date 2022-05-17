package com.c22_ce02.awmonitorapp.di

import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.api.ApiConfig
import com.c22_ce02.awmonitorapp.data.repository.CurrentAirQualityRepository
import com.c22_ce02.awmonitorapp.data.repository.CurrentConditionRepository
import com.c22_ce02.awmonitorapp.data.repository.FiveDaysOfDailyForecastRepository
import com.c22_ce02.awmonitorapp.data.repository.GeoPositionRepository

object Injection {
    fun provideGeoPositionRepository() : GeoPositionRepository {
        val apiService = ApiConfig.getApiService(BuildConfig.BASE_URL_ACCUWEATHER)
        return GeoPositionRepository(apiService)
    }

    fun provideFiveDaysOfDailyForecastRepository() : FiveDaysOfDailyForecastRepository {
        val apiService = ApiConfig.getApiService(BuildConfig.BASE_URL_ACCUWEATHER)
        return FiveDaysOfDailyForecastRepository(apiService)
    }

    fun provideCurrentConditionRepository() : CurrentConditionRepository {
        val apiService = ApiConfig.getApiService(BuildConfig.BASE_URL_ACCUWEATHER)
        return CurrentConditionRepository(apiService)
    }

    fun provideCurrentAirQualityRepository() : CurrentAirQualityRepository {
        val apiService = ApiConfig.getApiService(BuildConfig.BASE_URL_WEATHERBIT)
        return CurrentAirQualityRepository(apiService)
    }
}