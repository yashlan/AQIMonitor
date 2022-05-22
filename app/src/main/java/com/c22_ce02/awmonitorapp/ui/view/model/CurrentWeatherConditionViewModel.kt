package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.model.CurrentWeatherConditionResponse
import com.c22_ce02.awmonitorapp.data.repository.CurrentWeatherConditionRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrentWeatherConditionViewModel(private val repository: CurrentWeatherConditionRepository) :
    ViewModel() {
    fun getCurrentWeatherCondition(
        lat: Double,
        lon: Double,
        apiKey: String,
        onSuccess: (List<CurrentWeatherConditionResponse.Data>?) -> Unit,
        onFailed: (String?) -> Unit
    ) {
        val call = repository.getCurrentWeatherCondition(lat, lon, apiKey)
        call.enqueue(object : Callback<CurrentWeatherConditionResponse> {
            override fun onResponse(
                call: Call<CurrentWeatherConditionResponse>,
                response: Response<CurrentWeatherConditionResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body()?.data)
                } else {
                    onFailed(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<CurrentWeatherConditionResponse>, t: Throwable) {
                onFailed(t.message.toString())
            }
        })
    }
}