package com.c22_ce02.awmonitorapp.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.adapter.AirQualityForecastByHourAdapter
import com.c22_ce02.awmonitorapp.data.model.AirQualityForecastByHour
import com.c22_ce02.awmonitorapp.databinding.FragmentHomeBinding
import com.c22_ce02.awmonitorapp.ui.view.model.AirQualityForecastByHourViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.CurrentAirQualityViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.CurrentWeatherConditionViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.AirQualityForecastByHourViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.CurrentAirQualityViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.CurrentWeatherConditionViewModelFactory
import com.c22_ce02.awmonitorapp.utils.*
import com.c22_ce02.awmonitorapp.utils.Animation.startIncrementTextAnimation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var allowRefresh = false
    private var currentAQI = 0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val binding by viewBinding<FragmentHomeBinding>()
    private val currentWeatherConditionViewModel: CurrentWeatherConditionViewModel by viewModels {
        CurrentWeatherConditionViewModelFactory()
    }
    private val airQualityForecastByHourViewModel: AirQualityForecastByHourViewModel by viewModels {
        AirQualityForecastByHourViewModelFactory()
    }
    private val currentAirQualityViewModel: CurrentAirQualityViewModel by viewModels {
        CurrentAirQualityViewModelFactory()
    }

    private val listForecast = ArrayList<AirQualityForecastByHour>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                loadAllData()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                loadAllData()
            }
            else -> {
                showSnackBar(
                    binding.root,
                    R.string.msg_permission_maps,
                    R.string.yes,
                    onClickOkAction = {
                        allowRefresh = true
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", requireActivity().packageName, null)
                        intent.data = uri
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadAllData()
        binding.swipeRefresh.setOnRefreshListener {
            listForecast.clear()
            refreshFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) &&
            isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION) &&
            allowRefresh
        ) {
            loadAllData()
            allowRefresh = false
        } else {
            if (allowRefresh) {
                requestPermissionLauncher.launch(REQUIRED_PERMISSIONS_MAPS)
                allowRefresh = false
            }
        }
    }

    private fun refreshFragment() {
        Handler(Looper.getMainLooper()).postDelayed({
            parentFragmentManager
                .beginTransaction()
                .detach(this)
                .commitNow()
            parentFragmentManager
                .beginTransaction()
                .attach(this)
                .commitNow()
            binding.swipeRefresh.isRefreshing = false
        }, DELAY_REFRESH)
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

    private fun getItemStatusAirMessageBgColor(aqi: Int): Int {
        return ContextCompat.getColor(
            requireContext(),
            when (aqi) {
                in 0..100 -> R.color.warna_baik
                else -> R.color.deep_orange
            }
        )
    }

    private fun getStatusName(param: Int): String {
        return when (param) {
            in 0..50 -> getString(R.string.baik)
            in 51..100 -> getString(R.string.sedang)
            else -> getString(R.string.buruk)
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

    private fun getIconItem(param: Int): Int {
        return when (param) {
            in 0..50 -> R.drawable.ic_air_quality_item_status_baik
            in 51..100 -> R.drawable.ic_air_quality_item_status_sedang
            in 101..150 -> R.drawable.ic_air_quality_item_status_tidak_sehat
            in 151..300 -> R.drawable.ic_air_quality_item_status_sangat_tidak_sehat
            else -> R.drawable.ic_air_quality_item_status_berbahaya
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
        val adminArea = addresses[0].adminArea
        return "${subLocality ?: adminArea}, Indonesia"
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
                        showToast("lokasi tidak diketahui")
                    }
                }
                .addOnFailureListener {
                    showToast(it.localizedMessage?.toString() ?: it.message.toString())
                }
        }
    }

    private fun loadAllData() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocation(onGetLocation = { lat, lon ->
            currentWeatherConditionViewModel.getCurrentWeatherCondition(
                lat,
                lon,
                BuildConfig.API_KEY_WEATHERBIT,
                onSuccess = {
                    if (it != null) {
                        val data = it[0]
                        currentAQI = data.aqi.toInt()
                        changeWindowBackgroundResource(currentAQI)
                        with(binding) {
                            tvLocation.text = getCurrentLocationName(lat, lon)
                            tvDate.text = getCurrentDate()
                            itemPanelHomeInfo.itemStatusAirMessage.root.setCardBackgroundColor(
                                getItemStatusAirMessageBgColor(currentAQI)
                            )
                            itemPanelHomeInfo.itemStatusAirMessage.tvAirStatusMsg.text =
                                getAirStatusMessage(currentAQI)
                            itemInfoAirToday.tvToday.text = getString(R.string.hari_ini)
                            itemInfoAirToday.imgLabelAir.setImageResource(
                                getAQILabelStatus(
                                    currentAQI
                                )
                            )
                            itemInfoAirToday.root.setBackgroundResource(
                                getCardBgItemAirTodayResource(currentAQI)
                            )
                            itemInfoAirToday.tvWindSpeed.text =
                                convertWindSpeedToKmh(data.windSpeed.toInt())
                            "${data.temperature.toInt()} C".also { temp ->
                                itemInfoAirToday.tvTemperature.text = temp
                            }
                            "${data.humidity.toInt()}%".also { hum ->
                                itemInfoAirToday.tvHumidity.text = hum
                            }
                            startIncrementTextAnimation(currentAQI, itemInfoAirToday.tvAQI)
                        }
                        getForecastData(lat, lon)
                    }
                },
                onFailed = { errorMsg ->
                    if (errorMsg != null) {
                        showToast("currentWeatherConditionError : $errorMsg")
                    }
                }
            )

            currentAirQualityViewModel.getCurrentAirQuality(
                lat,
                lon,
                BuildConfig.API_KEY_WEATHERBIT,
                onSuccess = {
                    if (it != null) {
                        val data = it.data[0]
                        val itemListAirInfo = binding.itemPanelHomeInfo.itemInfoListAirToday

                        itemListAirInfo.tvPm10.text = data.pm10.toInt().toString()
                        itemListAirInfo.iconStatusPM10.setImageResource(getIconItem(data.pm10.toInt()))
                        itemListAirInfo.tvStatusPM10.text = getStatusName(data.pm10.toInt())

                        itemListAirInfo.tvPM25.text = data.pm25.toInt().toString()
                        itemListAirInfo.iconStatusPM25.setImageResource(getIconItem(data.pm25.toInt()))
                        itemListAirInfo.tvStatusPM25.text = getStatusName(data.pm25.toInt())

                        itemListAirInfo.tvSO2.text = data.so2.toInt().toString()
                        itemListAirInfo.iconStatusSO2.setImageResource(getIconItem(data.so2.toInt()))
                        itemListAirInfo.tvStatusSO2.text = getStatusName(data.so2.toInt())

                        itemListAirInfo.tvCO.text = data.co.toInt().toString()
                        itemListAirInfo.iconStatusCO.setImageResource(getIconItem(data.co.toInt()))
                        itemListAirInfo.tvStatusCO.text = getStatusName(data.co.toInt())

                        itemListAirInfo.tvNO2.text = data.no2.toInt().toString()
                        itemListAirInfo.iconStatusNO2.setImageResource(getIconItem(data.no2.toInt()))
                        itemListAirInfo.tvStatusNO2.text = getStatusName(data.no2.toInt())

                        itemListAirInfo.tvO3.text = data.o3.toInt().toString()
                        itemListAirInfo.iconStatusO3.setImageResource(getIconItem(data.o3.toInt()))
                        itemListAirInfo.tvStatusO3.text = getStatusName(data.o3.toInt())
                    }
                },
                onFailed = { errorMsg ->
                    if (errorMsg != null) {
                        showToast("currentAirQualityError : $errorMsg")
                    }
                }
            )
        })
    }

    private fun convertWindSpeedToKmh(speedMs: Int): String = "${speedMs * 3.6} km/h"

    private fun getForecastData(lat: Double, lon: Double) {
        airQualityForecastByHourViewModel.getAirQualityForecastByHour(
            lat,
            lon,
            BuildConfig.API_KEY_WEATHERBIT,
            HOURS,
            onSuccess = {
                if (it != null) {
                    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("id"))
                    val formatter = SimpleDateFormat("ha", Locale("id"))
                    it.forEach { data ->
                        val hour =
                            formatter.format(parser.parse(data.timestamp_local)!!).lowercase()
                        val currentHour = formatter.format(Date())
                        if(hour.equals(currentHour, true)) {
                            listForecast.add(
                                AirQualityForecastByHour(
                                    hour,
                                    getIconItem(currentAQI),
                                    currentAQI
                                )
                            )
                        }
                        else {
                            listForecast.add(
                                AirQualityForecastByHour(
                                    hour,
                                    getIconItem(data.aqi.toInt()),
                                    data.aqi.toInt()
                                )
                            )
                        }

                        if (it.size == listForecast.size) {
                            setupAdapter(
                                binding.itemPanelHomeInfo.rvListAirForecast,
                                false,
                                addAdapterValue = {
                                    binding.itemPanelHomeInfo.rvListAirForecast.adapter =
                                        AirQualityForecastByHourAdapter(listForecast)
                                })
                        }
                    }
                }
            },
            onFailed = { errorMsg ->
                if (errorMsg != null) {
                    showToast(" everyHourAirQualityForecastError : $errorMsg")
                }
            }
        )
    }

    companion object {
        private val REQUIRED_PERMISSIONS_MAPS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val DELAY_REFRESH: Long = 1000
        private const val HOURS = 6
    }
}