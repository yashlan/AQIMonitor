package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.response.CurrentAirQualityResponse
import com.c22_ce02.awmonitorapp.data.repository.CurrentAirQualityRepository
import retrofit2.*

class CurrentAirQualityViewModel(private val repository: CurrentAirQualityRepository) :
    ViewModel() {

    fun getCurrentAirQuality(
        lat: Double,
        lon: Double,
        onSuccess: (CurrentAirQualityResponse?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val call = repository.getCurrentAirQuality(lat, lon)
        call.enqueue(object : Callback<CurrentAirQualityResponse> {
            override fun onResponse(
                call: Call<CurrentAirQualityResponse>,
                response: Response<CurrentAirQualityResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    onError("Terjadi Kesalahan")
                }
            }

            override fun onFailure(call: Call<CurrentAirQualityResponse>, t: Throwable) {
                onError(t.localizedMessage?.toString() ?: t.message.toString())
            }
        })
    }
}