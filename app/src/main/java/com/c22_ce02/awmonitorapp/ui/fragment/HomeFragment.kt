package com.c22_ce02.awmonitorapp.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.adapter.AirQualityForecastTodayAdapter
import com.c22_ce02.awmonitorapp.data.model.ForecastAirQualityToday
import com.c22_ce02.awmonitorapp.databinding.FragmentHomeBinding
import com.c22_ce02.awmonitorapp.ui.view.model.CurrentAirQualityViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.CurrentConditionViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.FiveDaysOfDailyForecastViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.GeoPositionViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.CurrentAirQualityViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.CurrentConditionViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.FiveDaysOfDailyForecastViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.GeoPositionViewModelFactory
import com.c22_ce02.awmonitorapp.utils.*
import com.c22_ce02.awmonitorapp.utils.Animation.startIncrementTextAnimation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
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

    private val listForecast = ArrayList<ForecastAirQualityToday>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        loadAllData()
    }

    private fun initDummyForecast(todo: () -> Unit) {
        val currentHour = SimpleDateFormat("hh", Locale("id")).format(Date()).toInt()
        val amPmMarker = SimpleDateFormat("a", Locale("id")).format(Date()).lowercase()
        listForecast.add(
            ForecastAirQualityToday(
                "${currentHour}${amPmMarker}",
                R.drawable.ic_air_quality_item_status_baik,
                20
            )
        )
        listForecast.add(
            ForecastAirQualityToday(
                "${currentHour + 1}${amPmMarker}",
                R.drawable.ic_air_quality_item_status_sangat_tidak_sehat,
                254
            )
        )
        listForecast.add(
            ForecastAirQualityToday(
                "${currentHour + 2}${amPmMarker}",
                R.drawable.ic_air_quality_item_status_tidak_sehat,
                150
            )
        )
        listForecast.add(
            ForecastAirQualityToday(
                "${currentHour + 3}${amPmMarker}",
                R.drawable.ic_air_quality_item_status_baik,
                20
            )
        )
        listForecast.add(
            ForecastAirQualityToday(
                "${currentHour + 4}${amPmMarker}",
                R.drawable.ic_air_quality_item_status_sedang,
                60
            )
        )
        listForecast.add(
            ForecastAirQualityToday(
                "${currentHour + 5}${amPmMarker}",
                R.drawable.ic_air_quality_item_status_baik,
                26
            )
        )
        listForecast.add(
            ForecastAirQualityToday(
                "${currentHour + 6}${amPmMarker}",
                R.drawable.ic_air_quality_item_status_berbahaya,
                361
            )
        )

        todo.invoke()
    }

    private fun changeWindowBackgroundResource(aqi: Int) {
        requireActivity().window.decorView.setBackgroundResource(
            when (aqi) {
                in 0..50 -> R.drawable.window_bg_baik
                in 51..100 -> R.drawable.window_bg_sedang
                in 101..150 -> R.drawable.window_bg_tidak_sehat
                in 151..300 -> R.drawable.window_bg_sangat_tidak_sehat
                else -> R.drawable.window_bg_berbahaya
            }
        )
    }

    private fun getCardBgItemAirTodayResource(aqi: Int): Int {
        return when (aqi) {
            in 0..50 -> R.drawable.card_info_air_today_baik
            in 51..100 -> R.drawable.card_info_air_today_sedang
            in 101..150 -> R.drawable.card_info_air_today_tidak_sehat
            in 151..300 -> R.drawable.card_info_air_today_sangat_tidak_sehat
            else -> R.drawable.card_info_air_today_berbahaya
        }
    }

    private fun getAQILabelStatus(aqi: Int): Int {
        return when (aqi) {
            in 0..50 -> R.drawable.ic_label_baik
            in 51..100 -> R.drawable.ic_label_sedang
            in 101..150 -> R.drawable.ic_label_tidak_sehat
            in 151..300 -> R.drawable.ic_label_sangat_tidak_sehat
            else -> R.drawable.ic_label_berbahaya
        }
    }

    private fun getAirStatusMessage(aqi: Int): String {
        return when (aqi) {
            in 0..50 -> getString(R.string.status_air_baik_msg)
            in 51..100 -> getString(R.string.status_air_sedang_msg)
            in 101..150 -> getString(R.string.status_air_tidak_sehat_msg)
            in 151..300 -> getString(R.string.status_air_sangat_tidak_sehat_msg)
            else -> getString(R.string.status_air_berbahaya_msg)
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

    private fun getCurrentDate(): String {
        val dateNumber = SimpleDateFormat("dd", Locale("id")).format(Date())
        val yearNumber = SimpleDateFormat("yyyy", Locale("id")).format(Date())
        val dayName =
            Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale("id"))
        val monthName =
            Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("id"))
        return "$dayName, $dateNumber $monthName $yearNumber"
    }

    private fun getCurrentLocationName(lat: Double, lon: Double): String {
        val geocoder = Geocoder(requireContext(), Locale("id"))
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        val subLocality = addresses[0].subLocality
        return "$subLocality, Indonesia"
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
                        changeWindowBackgroundResource(data.aqi)
                        with(binding) {
                            binding.tvLocation.text = getCurrentLocationName(lat, lon)
                            tvDate.text = getCurrentDate()
                            itemStatusAirMessage.tvAirStatusMsg.text = getAirStatusMessage(data.aqi)
                            itemInfoAirToday.tvToday.text = getString(R.string.hari_ini)
                            itemInfoAirToday.imgLabelAir.setImageResource(getAQILabelStatus(data.aqi))
                            itemInfoAirToday.root.setBackgroundResource(
                                getCardBgItemAirTodayResource(data.aqi)
                            )
                            startIncrementTextAnimation(data.aqi, itemInfoAirToday.tvAQI)
                        }

                        initDummyForecast {
                            setupAdapter(binding.rvListAirForecast, false, addAdapterValue = {
                                binding.rvListAirForecast.adapter =
                                    AirQualityForecastTodayAdapter(listForecast)
                            })
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