package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.model.CurrentAirQualityResponse
import com.c22_ce02.awmonitorapp.data.repository.CurrentAirQualityRepository
import retrofit2.*

class CurrentAirQualityViewModel(private val repository: CurrentAirQualityRepository) :
    ViewModel() {
    fun getCurrentAirQuality(
        lat: Double,
        lon: Double,
        apikey: String,
        onSuccess: (CurrentAirQualityResponse?) -> Unit,
        onFailed: (String?) -> Unit
    ) {
        val call = repository.getCurrentAirQuality(lat, lon, apikey)
        call.enqueue(object : Callback<CurrentAirQualityResponse> {
            override fun onResponse(
                call: Call<CurrentAirQualityResponse>,
                response: Response<CurrentAirQualityResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    onFailed(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<CurrentAirQualityResponse>, t: Throwable) {
                onFailed(t.message.toString())
            }
        })
    }
}