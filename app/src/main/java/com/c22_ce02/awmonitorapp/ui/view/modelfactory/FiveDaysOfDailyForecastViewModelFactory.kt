package com.c22_ce02.awmonitorapp.ui.view.modelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c22_ce02.awmonitorapp.di.Injection
import com.c22_ce02.awmonitorapp.ui.view.model.FiveDaysOfDailyForecastViewModel

@Suppress("UNCHECKED_CAST")
class FiveDaysOfDailyForecastViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FiveDaysOfDailyForecastViewModel::class.java)) {
            FiveDaysOfDailyForecastViewModel(Injection.provideFiveDaysOfDailyForecastRepository()) as T
        } else {
            throw IllegalArgumentException("ViewModel of ${modelClass.simpleName} Not Found")
        }
    }
}