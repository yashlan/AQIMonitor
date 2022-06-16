package com.c22_ce02.awmonitorapp.ui.view.modelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c22_ce02.awmonitorapp.di.Injection
import com.c22_ce02.awmonitorapp.ui.view.model.WeatherForecastAndHistoryByHourViewModel

@Suppress("UNCHECKED_CAST")
class WeatherForecastAndHistoryByHourViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(WeatherForecastAndHistoryByHourViewModel::class.java)) {
            WeatherForecastAndHistoryByHourViewModel(Injection.provideWeatherForecastAndHistoryByHourRepository()) as T
        } else {
            throw IllegalArgumentException("ViewModel of ${modelClass.simpleName} Not Found")
        }
    }
}