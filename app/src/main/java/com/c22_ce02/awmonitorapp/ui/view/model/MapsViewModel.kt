package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.repository.MapsRepository
import com.c22_ce02.awmonitorapp.data.response.CurrentAirQuality34ProvinceResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapsViewModel(private val repository: MapsRepository) : ViewModel() {

    fun getCurrentAirQuality34Province(
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val call = repository.getCurrentAirQuality34Province()
        call.enqueue(object : Callback<CurrentAirQuality34ProvinceResponse> {
            override fun onResponse(
                call: Call<CurrentAirQuality34ProvinceResponse>,
                response: Response<CurrentAirQuality34ProvinceResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(Gson().toJson(response.body(), CurrentAirQuality34ProvinceResponse::class.java))
                } else {
                    onError("Terjadi Kesalahan")
                }
            }

            override fun onFailure(call: Call<CurrentAirQuality34ProvinceResponse>, t: Throwable) {
                onError(t.localizedMessage?.toString() ?: t.message.toString())
            }
        })
    }
}