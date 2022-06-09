package com.c22_ce02.awmonitorapp.ui.view.model



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.api.ApiConfig
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.data.response.CurrentItem
import com.c22_ce02.awmonitorapp.data.response.CurrentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapsViewModel: ViewModel() {


    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading : LiveData<Boolean> = _showLoading

    private val _currentData = MutableLiveData<List<CurrentItem>>()
    val currentData : LiveData<List<CurrentItem>> = _currentData



    fun getCurrentData(){
        _showLoading.value = true
        val client = ApiConfig.getApiService(BuildConfig.BASE_URL_AQIMonitor_GET).getCurrent()
        client.enqueue(object: Callback<CurrentResponse> {
            override fun onResponse(
                call: Call<CurrentResponse>,
                response: Response<CurrentResponse>
            ) {
                if (response.isSuccessful){
                    _showLoading.value = false
                    _currentData.value = response.body()?.data?.current
                }else {
                    Log.d("MapsFragment", "Error Response: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CurrentResponse>, t: Throwable) {
                _showLoading.value = false
                Log.d("onFailure", "OnFailure: ${t.message}")
            }

        })
    }

}