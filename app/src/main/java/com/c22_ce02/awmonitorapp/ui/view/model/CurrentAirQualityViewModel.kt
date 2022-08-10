package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.response.CurrentAirQualityResponse
import com.c22_ce02.awmonitorapp.data.repository.CurrentAirQualityRepository
import retrofit2.*
import kotlin.random.Random

class CurrentAirQualityViewModel : ViewModel() {

    fun getCurrentAirQuality(
        onSuccess: (CurrentAirQualityResponse?) -> Unit,
    ) {
        val data = CurrentAirQualityResponse(
            listOf(
                CurrentAirQualityResponse.Data(
                    aqi = Random.nextDouble(20.0, 300.0),
                    o3 = Random.nextDouble(20.0, 300.0),
                    so2 = Random.nextDouble(20.0, 300.0),
                    no2 = Random.nextDouble(20.0, 300.0),
                    co = Random.nextDouble(20.0, 300.0),
                    pm10 = Random.nextDouble(20.0, 300.0),
                    pm25 = Random.nextDouble(20.0, 300.0),
                )
            )
        )
        onSuccess.invoke(data)
    }
}