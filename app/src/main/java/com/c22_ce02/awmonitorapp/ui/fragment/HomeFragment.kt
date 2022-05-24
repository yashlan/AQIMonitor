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
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.adapter.AirQualityForecastByHourAdapter
import com.c22_ce02.awmonitorapp.data.model.AirQualityForecastByHour
import com.c22_ce02.awmonitorapp.data.model.CurrentAirQualityResponse
import com.c22_ce02.awmonitorapp.data.model.CurrentWeatherConditionResponse
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

    private var isCurrentWeatherConditionLoaded = false
    private var isAirQualityForecastByHourLoaded = false
    private var isCurrentAirQualityLoaded = false
    private var allowRefresh = false

    private lateinit var dataCurrentWeather: CurrentWeatherConditionResponse.Data
    private lateinit var dataCurrentAirQuality: CurrentAirQualityResponse.Data
    private val listForecast = ArrayList<AirQualityForecastByHour>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var refreshUITimer: Timer
    private val binding by viewBinding(FragmentHomeBinding::bind, onViewDestroyed = {
        refreshUITimer.cancel()
    })
    private val currentWeatherConditionViewModel: CurrentWeatherConditionViewModel by viewModels {
        CurrentWeatherConditionViewModelFactory()
    }
    private val airQualityForecastByHourViewModel: AirQualityForecastByHourViewModel by viewModels {
        AirQualityForecastByHourViewModelFactory()
    }
    private val currentAirQualityViewModel: CurrentAirQualityViewModel by viewModels {
        CurrentAirQualityViewModelFactory()
    }

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
        super.onViewCreated(view, savedInstanceState)
        resetAll {
            loadAllData()
            hideUI()
        }

        binding.swipeRefresh.setOnRefreshListener {
            hideUI()
            refreshFragment()
            setDefaultWindowBackgroundResource()
        }

        refreshUITimer = Timer()
        refreshUITimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                requireActivity().runOnUiThread {
                    if (isAllDataLoaded()) {
                        showUI()
                        updateUI()
                        cancel()
                    }
                }
            }
        }, 0, PERIOD_TIMER)
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

    private fun resetAll(onReset: () -> Unit) {
        listForecast.removeAll(listForecast)
        listForecast.clear()
        if (listForecast.isEmpty()) {
            onReset.invoke()
        }
    }

    private fun isAllDataLoaded(): Boolean =
        isCurrentAirQualityLoaded &&
                isAirQualityForecastByHourLoaded &&
                isCurrentWeatherConditionLoaded

    private fun refreshFragment() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.swipeRefresh.isRefreshing = false
            if (!binding.swipeRefresh.isRefreshing) {
/*                parentFragmentManager
                    .beginTransaction()
                    .detach(this)
                    .commitNow()
                parentFragmentManager
                    .beginTransaction()
                    .attach(this)
                    .commitNow()*/
                requireActivity().apply {
                    finish()
                    overridePendingTransition(0,0)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                }
            }
        }, DELAY_REFRESH)
    }

    private fun updateUI() {

        val currentAQI = dataCurrentWeather.aqi.toInt()
        val itemListAirInfo = binding.itemPanelHomeInfo.itemInfoListAirToday

        changeWindowBackgroundResource(currentAQI)
        with(binding) {
            startIncrementTextAnimation(currentAQI, itemInfoAirToday.tvAQI)

            val dataW = dataCurrentWeather

            getLocation(onGetLocation = { lat, lon ->
                tvLocation.text = getCurrentLocationName(lat, lon)
            })
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
                convertWindSpeedToKmh(dataW.windSpeed.toInt())
            "${dataW.temperature.toInt()} C".also { temp ->
                itemInfoAirToday.tvTemperature.text = temp
            }
            "${dataW.humidity.toInt()}%".also { hum ->
                itemInfoAirToday.tvHumidity.text = hum
            }


            with(itemListAirInfo) {

                val dataA = dataCurrentAirQuality

                tvLabelPM10.text = Text.spannableStringBuilder(
                    getString(R.string.pm10),
                    '1',
                    0.7f
                )
                tvPm10.text = dataA.pm10.toInt().toString()
                iconStatusPM10.setImageResource(getIconItem(dataA.pm10.toInt()))
                tvStatusPM10.text = getStatusName(dataA.pm10.toInt())

                tvLabelPM25.text = Text.spannableStringBuilder(
                    getString(R.string.pm25),
                    '2',
                    0.7f
                )
                tvPM25.text = dataA.pm25.toInt().toString()
                iconStatusPM25.setImageResource(getIconItem(dataA.pm25.toInt()))
                tvStatusPM25.text = getStatusName(dataA.pm25.toInt())

                tvLabelSO2.text = Text.spannableStringBuilder(
                    getString(R.string.so2),
                    '2',
                    0.7f
                )
                tvSO2.text = dataA.so2.toInt().toString()
                iconStatusSO2.setImageResource(getIconItem(dataA.so2.toInt()))
                tvStatusSO2.text = getStatusName(dataA.so2.toInt())

                tvLabelCO.text = getString(R.string.co)
                tvCO.text = dataA.co.toInt().toString()
                iconStatusCO.setImageResource(getIconItem(dataA.co.toInt()))
                tvStatusCO.text = getStatusName(dataA.co.toInt())

                tvLabelNO2.text = Text.spannableStringBuilder(
                    getString(R.string.no2),
                    '2',
                    0.7f
                )
                tvNO2.text = dataA.no2.toInt().toString()
                iconStatusNO2.setImageResource(getIconItem(dataA.no2.toInt()))
                tvStatusNO2.text = getStatusName(dataA.no2.toInt())

                tvLabelO3.text = Text.spannableStringBuilder(
                    getString(R.string.o3),
                    '3',
                    0.7f
                )
                tvO3.text = dataA.o3.toInt().toString()
                iconStatusO3.setImageResource(getIconItem(dataA.o3.toInt()))
                tvStatusO3.text = getStatusName(dataA.o3.toInt())
            }

            setupAdapter(
                binding.itemPanelHomeInfo.rvListAirForecast,
                false,
                addAdapterValue = {
                    binding.itemPanelHomeInfo.rvListAirForecast.adapter =
                        AirQualityForecastByHourAdapter(
                            listForecast,
                            canPlayAnim = isAllDataLoaded()
                        )
                }
            )
        }
    }

    private fun showUI() {
        with(binding) {
            tvLocation.visibility = View.VISIBLE
            tvDate.visibility = View.VISIBLE
            itemInfoAirToday.root.visibility = View.VISIBLE
            itemPanelHomeInfo.root.visibility = View.VISIBLE
            tvLocation.visibility = View.VISIBLE
            shimmerFragmentHome.stopShimmer()
            shimmerFragmentHome.visibility = View.GONE
        }
    }

    private fun hideUI() {
        with(binding) {
            tvLocation.visibility = View.INVISIBLE
            tvDate.visibility = View.INVISIBLE
            itemInfoAirToday.root.visibility = View.INVISIBLE
            itemPanelHomeInfo.root.visibility = View.INVISIBLE
            tvLocation.visibility = View.INVISIBLE
            shimmerFragmentHome.visibility = View.VISIBLE
            shimmerFragmentHome.startShimmer()
        }
    }

    private fun setDefaultWindowBackgroundResource() {
        binding.root.setBackgroundResource(R.color.shimmer_color_bg)
    }

    private fun changeWindowBackgroundResource(aqi: Int) {
        binding.root.setBackgroundResource(
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

            currentWeatherConditionViewModel.currentWeather.observe(requireActivity()) {
                if (it != null) {
                    dataCurrentWeather = it[0]
                    getForecastData(lat, lon)
                    isCurrentWeatherConditionLoaded = true
                }
            }

            currentWeatherConditionViewModel.errorMessage.observe(requireActivity()) {
                if (it != null) {
                    showToast(it)
                }
            }

            currentWeatherConditionViewModel.getCurrentWeatherCondition(
                lat,
                lon,
                BuildConfig.API_KEY_WEATHERBIT
            )

            currentAirQualityViewModel.currentAirQuality.observe(requireActivity()) {
                if (it != null) {
                    dataCurrentAirQuality = it.data[0]
                    isCurrentAirQualityLoaded = true
                }
            }

            currentAirQualityViewModel.errorMessage.observe(requireActivity()) {
                if (it != null) {
                    showToast(it)
                }
            }

            currentAirQualityViewModel.getCurrentAirQuality(
                lat,
                lon,
                BuildConfig.API_KEY_WEATHERBIT
            )
        })
    }

    private fun convertWindSpeedToKmh(speedMs: Int): String = "${speedMs * 3.6} km/h"

    private fun getForecastData(lat: Double, lon: Double) {
        airQualityForecastByHourViewModel.listForecast.observe(requireActivity()) {
            if (it != null) {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("id"))
                val formatter = SimpleDateFormat("ha", Locale("id"))
                it.forEach { data ->
                    val hour =
                        formatter.format(parser.parse(data.timestamp_local)!!).lowercase()
                    val currentHour = formatter.format(Date())
                    if (hour.equals(currentHour, true)) {
                        listForecast.add(
                            AirQualityForecastByHour(
                                hour,
                                getIconItem(dataCurrentWeather.aqi.toInt()),
                                dataCurrentWeather.aqi.toInt()
                            )
                        )
                    } else {
                        listForecast.add(
                            AirQualityForecastByHour(
                                hour,
                                getIconItem(data.aqi.toInt()),
                                data.aqi.toInt()
                            )
                        )
                    }
                    isAirQualityForecastByHourLoaded = it.size == listForecast.size
                }
            }
        }

        airQualityForecastByHourViewModel.errorMessage.observe(requireActivity()) {
            if (it != null) {
                showToast(it)
            }
        }

        airQualityForecastByHourViewModel.getAirQualityForecastByHour(
            lat,
            lon,
            BuildConfig.API_KEY_WEATHERBIT,
            HOURS
        )
    }

    companion object {
        private val REQUIRED_PERMISSIONS_MAPS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val DELAY_REFRESH: Long = 1000
        private const val PERIOD_TIMER: Long = 500
        private const val HOURS = 6
    }
}