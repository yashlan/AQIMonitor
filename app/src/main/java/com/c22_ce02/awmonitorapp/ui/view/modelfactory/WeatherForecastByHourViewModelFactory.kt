package com.c22_ce02.awmonitorapp.ui.view.modelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c22_ce02.awmonitorapp.di.Injection
import com.c22_ce02.awmonitorapp.ui.view.model.WeatherForecastByHourViewModel

@Suppress("UNCHECKED_CAST")
class WeatherForecastByHourViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(WeatherForecastByHourViewModel::class.java)) {
            WeatherForecastByHourViewModel(Injection.provideWeatherForecastByHourRepository()) as T
        } else {
            throw IllegalArgumentException("ViewModel of ${modelClass.simpleName} Not Found")
        }
    }
}