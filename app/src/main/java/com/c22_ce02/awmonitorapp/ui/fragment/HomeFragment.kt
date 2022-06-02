package com.c22_ce02.awmonitorapp.ui.fragment

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
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
import com.c22_ce02.awmonitorapp.adapter.AirQualityAndWeatherForecastByHourAdapter
import com.c22_ce02.awmonitorapp.data.model.AirQualityAndWeatherForecastByHour
import com.c22_ce02.awmonitorapp.data.model.AirQualityForecastByHour
import com.c22_ce02.awmonitorapp.data.model.WeatherForecastByHour
import com.c22_ce02.awmonitorapp.data.response.CurrentAirQualityResponse
import com.c22_ce02.awmonitorapp.data.response.CurrentWeatherConditionResponse
import com.c22_ce02.awmonitorapp.databinding.FragmentHomeBinding
import com.c22_ce02.awmonitorapp.ui.view.model.AirQualityForecastByHourViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.CurrentAirQualityViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.CurrentWeatherConditionViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.WeatherForecastByHourViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.AirQualityForecastByHourViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.CurrentAirQualityViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.CurrentWeatherConditionViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.WeatherForecastByHourViewModelFactory
import com.c22_ce02.awmonitorapp.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home), LocationListener {

    private var isCurrentWeatherConditionLoaded = false
    private var isAirQualityForecastByHourLoaded = false
    private var isWeatherForecastByHourLoaded = false
    private var isCurrentAirQualityLoaded = false
    private var isLocationChanged = false
    private var allowRefresh = false

    private var newLat = 0.0
    private var newLon = 0.0

    private lateinit var dataCurrentWeather: CurrentWeatherConditionResponse.Data
    private lateinit var dataCurrentAirQuality: CurrentAirQualityResponse.Data
    private val listForecastAir = ArrayList<AirQualityForecastByHour>()
    private val listForecastWeather = ArrayList<WeatherForecastByHour>()
    private val listForecastAirAndWeather = ArrayList<AirQualityAndWeatherForecastByHour>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private var refreshUITimer: Timer? = null
    private var refreshLocationTimer: Timer? = null
    private var refreshFragmentHandler: Handler? = null

    private val binding by viewBinding(FragmentHomeBinding::bind, onViewDestroyed = {
        refreshUITimer?.cancel()
        refreshLocationTimer?.cancel()
        refreshFragmentHandler?.removeCallbacksAndMessages(null)
    })
    private val currentWeatherConditionViewModel: CurrentWeatherConditionViewModel by viewModels {
        CurrentWeatherConditionViewModelFactory()
    }
    private val airQualityForecastByHourViewModel: AirQualityForecastByHourViewModel by viewModels {
        AirQualityForecastByHourViewModelFactory()
    }
    private val weatherForecastByHourViewModel: WeatherForecastByHourViewModel by viewModels {
        WeatherForecastByHourViewModelFactory()
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

        binding.swipeRefresh.setOnRefreshListener {
            hideUI()
            refreshFragment()
            setDefaultWindowBackgroundResource()
        }

        if (!isNetworkAvailable(requireContext(), showNotAvailableInfo = true)) {
            hideUI()
            binding.shimmerFragmentHome.hideShimmer()
            return
        }

        resetAll {
            loadAllData()
            hideUI()
        }

        refreshUITimer = Timer()
        refreshLocationTimer = Timer()

        refreshUITimer?.scheduleAtFixedRate(object : TimerTask() {
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
        listForecastAirAndWeather.clear()
        listForecastAirAndWeather.removeAll(listForecastAirAndWeather)

        listForecastWeather.clear()
        listForecastWeather.removeAll(listForecastWeather)

        listForecastAir.clear()
        listForecastAir.removeAll(listForecastAir)

        if (listForecastAir.isEmpty() &&
            listForecastWeather.isEmpty() &&
            listForecastAirAndWeather.isEmpty()
        ) {
            onReset.invoke()
        }
    }

    private fun isAllDataLoaded(): Boolean =
        isCurrentAirQualityLoaded &&
                isAirQualityForecastByHourLoaded &&
                isCurrentWeatherConditionLoaded &&
                isWeatherForecastByHourLoaded

    private fun refreshFragment() {
        refreshFragmentHandler = Handler(Looper.getMainLooper())
        refreshFragmentHandler?.postDelayed({
            binding.swipeRefresh.isRefreshing = false
            if (!binding.swipeRefresh.isRefreshing) {
                requireActivity().apply {
                    finish()
                    overridePendingTransition(0, 0)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
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
                itemLocationAndDate.tvLocation.text = getCurrentLocationName(lat, lon)
            })
            itemLocationAndDate.tvDate.text = getCurrentDate()
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

            startIncrementTextAnimation(
                convertWindSpeedToKmh(dataW.windSpeed.toInt()),
                " km/h",
                itemInfoAirToday.tvWindSpeed
            )
            startIncrementTextAnimation(
                dataW.temperature.toInt(),
                " C",
                itemInfoAirToday.tvTemperature
            )
            startIncrementTextAnimation(
                dataW.humidity.toInt(),
                "%",
                itemInfoAirToday.tvHumidity
            )

            with(itemListAirInfo) {

                val dataA = dataCurrentAirQuality

                tvLabelPM10.text = spannableStringBuilder(
                    getString(R.string.pm10),
                    '1',
                    0.7f
                )
                startIncrementTextAnimation(dataA.pm10.toInt(), tvPm10)
                iconStatusPM10.setImageResource(getIconItem(dataA.pm10.toInt()))
                tvStatusPM10.text = getStatusName(dataA.pm10.toInt())

                tvLabelPM25.text = spannableStringBuilder(
                    getString(R.string.pm25),
                    '2',
                    0.7f
                )
                startIncrementTextAnimation(dataA.pm25.toInt(), tvPM25)
                iconStatusPM25.setImageResource(getIconItem(dataA.pm25.toInt()))
                tvStatusPM25.text = getStatusName(dataA.pm25.toInt())

                tvLabelSO2.text = spannableStringBuilder(
                    getString(R.string.so2),
                    '2',
                    0.7f
                )
                startIncrementTextAnimation(dataA.so2.toInt(), tvSO2)
                iconStatusSO2.setImageResource(getIconItem(dataA.so2.toInt()))
                tvStatusSO2.text = getStatusName(dataA.so2.toInt())

                tvLabelCO.text = getString(R.string.co)
                startIncrementTextAnimation(dataA.co.toInt(), tvCO)
                iconStatusCO.setImageResource(getIconItem(dataA.co.toInt()))
                tvStatusCO.text = getStatusName(dataA.co.toInt())

                tvLabelNO2.text = spannableStringBuilder(
                    getString(R.string.no2),
                    '2',
                    0.7f
                )
                startIncrementTextAnimation(dataA.no2.toInt(), tvNO2)
                iconStatusNO2.setImageResource(getIconItem(dataA.no2.toInt()))
                tvStatusNO2.text = getStatusName(dataA.no2.toInt())

                tvLabelO3.text = spannableStringBuilder(
                    getString(R.string.o3),
                    '3',
                    0.7f
                )
                startIncrementTextAnimation(dataA.o3.toInt(), tvO3)
                iconStatusO3.setImageResource(getIconItem(dataA.o3.toInt()))
                tvStatusO3.text = getStatusName(dataA.o3.toInt())
            }

            for (i in 0 until HOURS) {
                listForecastAirAndWeather.add(
                    AirQualityAndWeatherForecastByHour(
                        listForecastAir[i],
                        listForecastWeather[i]
                    )
                )

                if (listForecastAirAndWeather.size == HOURS) {
                    setupAdapter(
                        binding.itemPanelHomeInfo.rvListAirForecast,
                        false,
                        addAdapterValue = {
                            binding.itemPanelHomeInfo.rvListAirForecast.adapter =
                                AirQualityAndWeatherForecastByHourAdapter(
                                    listForecastAirAndWeather,
                                    canPlayAnim = isAllDataLoaded()
                                )
                        }
                    )
                }
            }
        }
    }

    private fun showUI() {
        with(binding) {
            itemLocationAndDate.tvLocation.visibility = View.VISIBLE
            itemLocationAndDate.tvDate.visibility = View.VISIBLE
            itemInfoAirToday.root.visibility = View.VISIBLE
            itemPanelHomeInfo.root.visibility = View.VISIBLE
            shimmerFragmentHome.visibility = View.GONE
        }
    }

    private fun hideUI() {
        with(binding) {
            itemLocationAndDate.tvLocation.visibility = View.INVISIBLE
            itemLocationAndDate.tvDate.visibility = View.INVISIBLE
            itemInfoAirToday.root.visibility = View.INVISIBLE
            itemPanelHomeInfo.root.visibility = View.INVISIBLE
            shimmerFragmentHome.visibility = View.VISIBLE
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
        val dateNumber = SimpleDateFormat("d", Locale("id")).format(Date())
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
        val result = if (addresses.size > 0) {
            val adminArea =
                if (addresses[0].adminArea != null) addresses[0].adminArea else "Tidak Diketahui"
            val subLocality =
                if (addresses[0].subLocality != null) addresses[0].subLocality else adminArea
            val country = addresses[0].countryName
            "$subLocality, $country"
        } else {
            "Tidak Diketahui"
        }
        return result
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
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                        return CancellationTokenSource().token
                    }

                    override fun isCancellationRequested(): Boolean {
                        return false
                    }
                })
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        onGetLocation(location.latitude, location.longitude)
                    } else {
                        requestLocation()
                        refreshLocationTimer?.scheduleAtFixedRate(object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    if (isLocationChanged) {
                                        onGetLocation(newLat, newLon)
                                        showToast("lokasi berhasil ditemukan")
                                        cancel()
                                    }
                                }
                            }
                        }, 0, PERIOD_TIMER)
                    }
                }
                .addOnFailureListener {
                    showToast(it.localizedMessage?.toString() ?: it.message.toString())
                }
        }
    }

    override fun onLocationChanged(location: Location) {
        newLat = location.latitude
        newLon = location.longitude
        isLocationChanged = true
        locationManager.removeUpdates(this)
    }

    private fun requestLocation() {
        locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(REQUIRED_PERMISSIONS_MAPS)
            } else {
                locationManager.requestLocationUpdates(
                    GPS_PROVIDER,
                    10000L,
                    1000f,
                    this
                )
                showToast("Sedang Mencari Lokasi")
            }
        } else {
            binding.shimmerFragmentHome.stopShimmer()
            showSnackBar(
                binding.root,
                R.string.msg_permission_maps,
                R.string.yes,
                onClickOkAction = {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            )
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
                    if (BuildConfig.DEBUG) {
                        Timber.e(it)
                    }
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
                    if (BuildConfig.DEBUG) {
                        Timber.e(it)
                    }
                }
            }

            currentAirQualityViewModel.getCurrentAirQuality(
                lat,
                lon,
                BuildConfig.API_KEY_WEATHERBIT
            )
        })
    }

    private fun convertWindSpeedToKmh(speedMs: Int): Int = (speedMs * 3.6).toInt()

    private fun getForecastData(lat: Double, lon: Double) {
        airQualityForecastByHourViewModel.listForecast.observe(requireActivity()) {
            if (it != null) {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("id"))
                val formatter = SimpleDateFormat("ha", Locale("id"))
                it.forEach { data ->
                    val hour =
                        formatter.format(parser.parse(data.timestamp_local)!!).lowercase()
                    val currentHour = formatter.format(Date()).lowercase()
                    if (hour.equals(currentHour, true)) {
                        listForecastAir.add(
                            AirQualityForecastByHour(
                                hour = currentHour,
                                iconAQISrc = getIconItem(dataCurrentWeather.aqi.toInt()),
                                aqi = dataCurrentWeather.aqi.toInt(),
                                pm10 = dataCurrentAirQuality.pm10.toInt(),
                                pm25 = dataCurrentAirQuality.pm25.toInt(),
                                o3 = dataCurrentAirQuality.o3.toInt(),
                                so2 = dataCurrentAirQuality.so2.toInt(),
                                no2 = dataCurrentAirQuality.no2.toInt(),
                                co = dataCurrentAirQuality.co.toInt()
                            )
                        )
                    } else {
                        listForecastAir.add(
                            AirQualityForecastByHour(
                                hour = hour,
                                iconAQISrc = getIconItem(data.aqi.toInt()),
                                aqi = data.aqi.toInt(),
                                pm10 = data.pm10.toInt(),
                                pm25 = data.pm25.toInt(),
                                o3 = data.o3.toInt(),
                                so2 = data.so2.toInt(),
                                no2 = data.no2.toInt(),
                                co = data.co.toInt()
                            )
                        )
                    }
                    isAirQualityForecastByHourLoaded = it.size == listForecastAir.size
                }
            }
        }

        airQualityForecastByHourViewModel.errorMessage.observe(requireActivity()) {
            if (it != null) {
                if (BuildConfig.DEBUG) {
                    Timber.e(it)
                }
            }
        }

        airQualityForecastByHourViewModel.getAirQualityForecastByHour(
            lat,
            lon,
            BuildConfig.API_KEY_WEATHERBIT,
            HOURS
        )

        weatherForecastByHourViewModel.listForecast.observe(requireActivity()) {
            if (it != null) {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("id"))
                val formatter = SimpleDateFormat("ha", Locale("id"))
                it.forEach { data ->
                    val hour =
                        formatter.format(parser.parse(data.timestamp_local)!!).lowercase()
                    val currentHour = formatter.format(Date()).lowercase()
                    if (hour.equals(currentHour, true)) {
                        listForecastWeather.add(
                            WeatherForecastByHour(
                                windSpeed = dataCurrentWeather.windSpeed.toInt(),
                                humidity = dataCurrentWeather.humidity.toInt(),
                                temperature = dataCurrentWeather.temperature.toInt()
                            )
                        )
                    } else {
                        listForecastWeather.add(
                            WeatherForecastByHour(
                                windSpeed = data.windSpeed.toInt(),
                                humidity = data.humidity.toInt(),
                                temperature = data.temperature.toInt()
                            )
                        )
                    }
                    isWeatherForecastByHourLoaded = it.size == listForecastWeather.size
                }
            }
        }

        weatherForecastByHourViewModel.errorMessage.observe(requireActivity()) {
            if (it != null) {
                if (BuildConfig.DEBUG) {
                    Timber.e(it)
                }
            }
        }

        weatherForecastByHourViewModel.getWeatherForecastByHour(
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
        const val FORECAST_EXTRA = "FORECAST_EXTRA"
    }
}