package com.c22_ce02.awmonitorapp.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.FragmentHomeBinding
import com.c22_ce02.awmonitorapp.ui.activity.HomeActivity
import com.c22_ce02.awmonitorapp.ui.view.model.CurrentAirQualityViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.CurrentConditionViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.FiveDaysOfDailyForecastViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.GeoPositionViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.CurrentAirQualityViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.CurrentConditionViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.FiveDaysOfDailyForecastViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.GeoPositionViewModelFactory
import com.c22_ce02.awmonitorapp.utils.requestPermissionLauncher
import com.c22_ce02.awmonitorapp.utils.showToast
import com.c22_ce02.awmonitorapp.utils.viewBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.color.DynamicColors
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val binding by viewBinding<FragmentHomeBinding>()
    private val geoPositionViewModel: GeoPositionViewModel by viewModels {
        GeoPositionViewModelFactory()
    }
    private val fiveDaysOfDailyForecastViewModel: FiveDaysOfDailyForecastViewModel by viewModels {
        FiveDaysOfDailyForecastViewModelFactory()
    }
    private val currentConditionViewModel: CurrentConditionViewModel by viewModels {
        CurrentConditionViewModelFactory()
    }
    private val currentAirQualityViewModel: CurrentAirQualityViewModel by viewModels {
        CurrentAirQualityViewModelFactory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        loadAllData()
    }

    private fun changeBackgroundColor(aqi: Int) {
        requireActivity().window.decorView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                when (aqi) {
                    in 0..50 -> R.color.warna_baik
                    in 51..100 -> R.color.warna_sedang
                    in 101..150 -> R.color.warna_tidak_sehat
                    in 151..300 -> R.color.warna_sangat_tidak_sehat
                    else -> R.color.warna_berbahaya
                }
            )
        )
    }

    private fun getAQIStatus(aqi: Int) : String {
        return when (aqi) {
            in 0..50 -> "Baik"
            in 51..100 -> "Sedang"
            in 101..150 -> "Tidak Sehat"
            in 151..300 -> "Sangat Tidak \nSehat"
            else -> "Berbahaya"
        }
    }

    private fun getLocation(onGetLocation: (Double, Double) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS_MAPS)
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        onGetLocation(location.latitude, location.longitude)
                    } else {
                        showToast("lokasi null")
                    }
                }
                .addOnFailureListener {
                    showToast("Tidak dapat menemukan lokasi. Silakan Coba Lagi")
                }
        }
    }

    private fun getCurrentDate() : String {
        val dateNumber = SimpleDateFormat("dd", Locale("id")).format(Date())
        val yearNumber = SimpleDateFormat("yyyy", Locale("id")).format(Date())
        val dayName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale("id"))
        val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("id"))
        return "$dayName, $dateNumber $monthName $yearNumber"
    }

    private fun loadAllData() {
        getLocation(onGetLocation = { lat, lon ->
            currentAirQualityViewModel.getCurrentAirQuality(
                lat,
                lon,
                BuildConfig.API_KEY_WEATHERBIT,
                onSuccess = {
                    if (it != null) {
                        val data = it.data[0]
                        changeBackgroundColor(data.aqi)
                        with(binding) {
                            tvDate.text = getCurrentDate()
                            tvAQI.text = data.aqi.toString()
                            tvAQIStatus.text = getAQIStatus(data.aqi)
                            panelInfoAirToday.root.setBackgroundResource(
                                when (data.aqi) {
                                    in 0..50 -> R.drawable.panel_info_air_today_baik
                                    in 51..100 -> R.drawable.panel_info_air_today_sedang
                                    in 101..150 -> R.drawable.panel_info_air_today_tidak_sehat
                                    in 151..300 -> R.drawable.panel_info_air_today_sangat_tidak_sehat
                                    else -> R.drawable.panel_info_air_today_berbahaya
                                }
                            )
                        }
                    }
                },
                onFailed = { errorMsg ->
                    if (errorMsg != null) {
                        showToast("currentAirQualityError : $errorMsg")
                    }
                }
            )

            /* geoPositionViewModel.getInformationUserByGeoPosition(
                 "${lat},${lon}",
                 onSuccess = { r ->
                     binding.tvMyLocation.text =
                         "Lokasi Saya : ${r?.localizedName}, " +
                                 "${r?.administrativeArea?.localizedName}, " +
                                 "${r?.country?.localizedName}"

                     if (r?.locationKey != null) {
                         currentConditionViewModel.getCurrentCondition(
                             r.locationKey,
                             onSuccess = {
                                 val tempC =
                                     "${it?.get(0)?.temperature?.metric?.value}${it?.get(0)?.temperature?.metric?.unit}"
                                 binding.tvCurrentCondition.text =
                                     "Cuaca Saat Ini : ${it?.get(0)?.weatherText}, $tempC"
                             },
                             onFailed = { errorMsg ->
                                 if (errorMsg != null) {
                                     showToast("currentConditionError : $errorMsg")
                                 }
                             }
                         )

                         fiveDaysOfDailyForecastViewModel.get5DaysOfDailyForecasts(
                             r.locationKey,
                             onSuccess = {
                                 val f0 =
                                     "${it?.dailyForecasts?.get(0)?.temperature?.minimum?.value}F - ${
                                         it?.dailyForecasts?.get(0)?.temperature?.maximum?.value
                                     }F"
                                 val f1 =
                                     "${it?.dailyForecasts?.get(1)?.temperature?.minimum?.value}F - ${
                                         it?.dailyForecasts?.get(1)?.temperature?.maximum?.value
                                     }F"
                                 val f2 =
                                     "${it?.dailyForecasts?.get(2)?.temperature?.minimum?.value}F - ${
                                         it?.dailyForecasts?.get(2)?.temperature?.maximum?.value
                                     }F"
                                 val f3 =
                                     "${it?.dailyForecasts?.get(3)?.temperature?.minimum?.value}F - ${
                                         it?.dailyForecasts?.get(3)?.temperature?.maximum?.value
                                     }F"
                                 val f4 =
                                     "${it?.dailyForecasts?.get(4)?.temperature?.minimum?.value}F - ${
                                         it?.dailyForecasts?.get(4)?.temperature?.maximum?.value
                                     }F"

                                 with(binding) {
                                     tvForecastDay1.text = "Perkiraan Suhu : $f0"
                                     tvForecastDay2.text = "Perkiraan Suhu : $f1"
                                     tvForecastDay3.text = "Perkiraan Suhu : $f2"
                                     tvForecastDay4.text = "Perkiraan Suhu : $f3"
                                     tvForecastDay5.text = "Perkiraan Suhu : $f4"
                                 }
                             },
                             onFailed = { errorMsg ->
                                 if (errorMsg != null) {
                                     showToast("FiveDaysForecastWeatherError : $errorMsg")
                                 }
                             }
                         )
                     }
                 },
                 onFailed = { errorMsg ->
                     if (errorMsg != null) {
                         showToast("informationByGeoPositionError : $errorMsg")
                     }
                 })*/
        })
    }

    companion object {
        private val REQUIRED_PERMISSIONS_MAPS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}