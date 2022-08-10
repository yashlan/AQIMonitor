package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.response.CurrentWeatherConditionResponse
import com.c22_ce02.awmonitorapp.data.repository.CurrentWeatherConditionRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class CurrentWeatherConditionViewModel : ViewModel() {

    fun getCurrentWeatherCondition(
        onSuccess: (List<CurrentWeatherConditionResponse.Data>?) -> Unit,
    ) {
        val data = listOf(CurrentWeatherConditionResponse.Data(
            aqi = Random.nextDouble(1.0, 350.0),
            humidity = 75.5,
            windSpeed = 20.3,
            temperature = 32.3,
            obTime = "2022-10-08 16:45"
        ))
        onSuccess.invoke(data)
    }
}