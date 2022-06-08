package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.response.CurrentWeatherConditionResponse
import com.c22_ce02.awmonitorapp.data.repository.CurrentWeatherConditionRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrentWeatherConditionViewModel(private val repository: CurrentWeatherConditionRepository) :
    ViewModel() {

    fun getCurrentWeatherCondition(
        lat: Double,
        lon: Double,
        onSuccess: (List<CurrentWeatherConditionResponse.Data>?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val call = repository.getCurrentWeatherCondition(lat, lon)
        call.enqueue(object : Callback<CurrentWeatherConditionResponse> {
            override fun onResponse(
                call: Call<CurrentWeatherConditionResponse>,
                response: Response<CurrentWeatherConditionResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body()?.data)
                } else {
                    onError("Terjadi Kesalahan")
                }
            }

            override fun onFailure(call: Call<CurrentWeatherConditionResponse>, t: Throwable) {
                onError(t.localizedMessage?.toString() ?: t.message.toString())
            }
        })
    }
}