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
import android.view.animation.AlphaAnimation
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.adapter.AirQualityAndWeatherForecastByHourAdapter
import com.c22_ce02.awmonitorapp.data.model.AirQualityAndWeatherHistoryForecastByHour
import com.c22_ce02.awmonitorapp.data.model.AirQualityHistoryAndForecastByHour
import com.c22_ce02.awmonitorapp.data.model.WeatherHistoryAndForecastByHour
import com.c22_ce02.awmonitorapp.data.preference.CheckPreference
import com.c22_ce02.awmonitorapp.data.preference.PostDataPreference
import com.c22_ce02.awmonitorapp.data.response.CurrentAirQualityResponse
import com.c22_ce02.awmonitorapp.data.response.CurrentWeatherConditionResponse
import com.c22_ce02.awmonitorapp.databinding.FragmentHomeBinding
import com.c22_ce02.awmonitorapp.ui.activity.DetailArticleActivity
import com.c22_ce02.awmonitorapp.ui.view.model.AirQualityForecastAndHistoryByHourViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.CurrentAirQualityViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.CurrentWeatherConditionViewModel
import com.c22_ce02.awmonitorapp.ui.view.model.WeatherForecastAndHistoryByHourViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.AirQualityForecastAndHistoryByHourViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.CurrentAirQualityViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.CurrentWeatherConditionViewModelFactory
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.WeatherForecastAndHistoryByHourViewModelFactory
import com.c22_ce02.awmonitorapp.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.hours


class HomeFragment : Fragment(R.layout.fragment_home), LocationListener {

    private var isCurrentWeatherConditionLoaded = false
    private var isAirQualityHistoryAndForecastByHourLoaded = false
    private var isWeatherHistoryAndForecastByHourLoaded = false
    private var isCurrentAirQualityLoaded = false
    private var isLocationChanged = false
    private var allowRefresh = false

    private var newLat = 0.0
    private var newLon = 0.0

    private lateinit var dataCurrentWeather: CurrentWeatherConditionResponse.Data
    private lateinit var dataCurrentAirQuality: CurrentAirQualityResponse.Data
    private val listHistoryAndForecastAir = ArrayList<AirQualityHistoryAndForecastByHour>()
    private val listForecastAndHistoryWeather = ArrayList<WeatherHistoryAndForecastByHour>()
    private val listHistoryForecastAirAndWeather =
        ArrayList<AirQualityAndWeatherHistoryForecastByHour>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private var refreshUITimer: Timer? = null
    private var refreshLocationTimer: Timer? = null
    private var refreshFragmentHandler: Handler? = null
    private var guide1Handler: Handler? = null
    private var guide2Handler: Handler? = null
    private var callApiHandler: Handler? = null

    private val binding by viewBinding(FragmentHomeBinding::bind, onViewDestroyed = {
        refreshUITimer?.cancel()
        refreshLocationTimer?.cancel()
        refreshFragmentHandler?.removeCallbacksAndMessages(null)
        guide1Handler?.removeCallbacksAndMessages(null)
        guide2Handler?.removeCallbacksAndMessages(null)
        callApiHandler?.removeCallbacksAndMessages(null)
    })
    private val currentWeatherConditionViewModel: CurrentWeatherConditionViewModel by viewModels {
        CurrentWeatherConditionViewModelFactory()
    }
    private val weatherForecastAndHistoryByHourViewModel: WeatherForecastAndHistoryByHourViewModel by viewModels {
        WeatherForecastAndHistoryByHourViewModelFactory()
    }
    private val airQualityForecastAndHistoryByHourViewModel: AirQualityForecastAndHistoryByHourViewModel by viewModels {
        AirQualityForecastAndHistoryByHourViewModelFactory()
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

    override fun onDestroy() {
        super.onDestroy()
        refreshUITimer?.cancel()
        refreshLocationTimer?.cancel()
        refreshFragmentHandler?.removeCallbacksAndMessages(null)
        guide1Handler?.removeCallbacksAndMessages(null)
        guide2Handler?.removeCallbacksAndMessages(null)
        callApiHandler?.removeCallbacksAndMessages(null)
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

        locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(GPS_PROVIDER)) {
            hideUI()
            binding.shimmerFragmentHome.hideShimmer()
            showSnackBar(
                binding.root,
                R.string.msg_permission_gps,
                R.string.yes,
                onClickOkAction = {
                    allowRefresh = true
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            )
            return
        }

        resetAll()
        {
            hideUI()
            callApiHandler = Handler(Looper.getMainLooper())
            callApiHandler?.postDelayed({
                loadAllData()
            }, DELAY_CALL_API)
        }

        refreshUITimer = Timer()
        refreshLocationTimer = Timer()

        refreshUITimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    requireActivity().runOnUiThread {
                        if (isAllDataLoaded()) {
                            showUI()
                            updateUI()
                            postDataWeatherAndAirQuality()
                            setupGuide()
                            cancel()
                        }
                    }
                }
            }, 0, PERIOD_TIMER
        )
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

    private fun postDataWeatherAndAirQuality() {
        val postPref = PostDataPreference(requireContext())
        val diff = Date().time - postPref.getLastPost()
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        val canPostData = hours >= 1

        if (!canPostData) return

        getLocation(onGetLocation = { lat, lon ->
            getCurrentLocationName(lat, lon, onGetLocationName = { locationName ->
                PostData(this@HomeFragment).postCurrentWeatherAndAirData(
                    location = locationName,
                    date = dataCurrentWeather.obTime,
                    aqi = dataCurrentAirQuality.aqi,
                    o3 = dataCurrentAirQuality.o3,
                    so2 = dataCurrentAirQuality.so2,
                    no2 = dataCurrentAirQuality.no2,
                    co = dataCurrentAirQuality.so2,
                    pm10 = dataCurrentAirQuality.pm10,
                    pm25 = dataCurrentAirQuality.pm25,
                    temperature = dataCurrentWeather.temperature,
                    humidity = dataCurrentWeather.humidity,
                    windSpeed = dataCurrentWeather.windSpeed,
                    onSuccessCallback = {
                        if (BuildConfig.DEBUG) {
                            showToast("post data history success!")
                        }
                        postPref.setLastPost(Date().time)
                    }
                )
            })
        })
    }

    private fun resetAll(onReset: () -> Unit) {
        listHistoryForecastAirAndWeather.clear()
        listHistoryForecastAirAndWeather.removeAll(listHistoryForecastAirAndWeather)

        listForecastAndHistoryWeather.clear()
        listForecastAndHistoryWeather.removeAll(listForecastAndHistoryWeather)

        listHistoryAndForecastAir.clear()
        listHistoryAndForecastAir.removeAll(listHistoryAndForecastAir)

        if (listHistoryAndForecastAir.isEmpty() &&
            listForecastAndHistoryWeather.isEmpty() &&
            listHistoryForecastAirAndWeather.isEmpty()
        ) {
            onReset.invoke()
        }
    }

    private fun isAllDataLoaded(): Boolean =
        isCurrentAirQualityLoaded &&
                isAirQualityHistoryAndForecastByHourLoaded &&
                isWeatherHistoryAndForecastByHourLoaded &&
                isCurrentWeatherConditionLoaded

    private fun refreshFragment() {
        refreshFragmentHandler = Handler(Looper.getMainLooper())
        refreshFragmentHandler?.postDelayed({
            binding.swipeRefresh.isRefreshing = false
            if (!binding.swipeRefresh.isRefreshing) {
                val navHostFragment =
                    requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home_activity)
                navHostFragment?.findNavController()?.navigate(R.id.navigation_home)
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

            itemPanelHomeInfo.itemStatusAirMessage.root.setOnClickListener {
                val checkPreference = CheckPreference(requireContext())
                val checkHelper = checkPreference.getCheckGuide()
                if (dataCurrentAirQuality.aqi.toInt() > 100 && checkHelper.isUserFinishGuide) {
                    it.startAnimation(AlphaAnimation(1f, .5f))
                    val url =
                        "https://aqimonitorblogs.blogspot.com/2022/06/10-cara-mengurangi-polusi-udara-yang.html"
                    val i = Intent(requireContext(), DetailArticleActivity::class.java)
                    i.putExtra(URL_EXTRA, url)
                    startActivity(i)
                }
            }

            for (i in 0 until TOTAL_LIST_FORECAST_AND_HISTORY_SIZE) {
                listHistoryForecastAirAndWeather.add(
                    AirQualityAndWeatherHistoryForecastByHour(
                        listHistoryAndForecastAir[i],
                        listForecastAndHistoryWeather[i]
                    )
                )

                if (listHistoryForecastAirAndWeather.size == TOTAL_LIST_FORECAST_AND_HISTORY_SIZE) {
                    setupAdapter(
                        binding.itemPanelHomeInfo.rvListAirForecast,
                        false,
                        addAdapterValue = {
                            binding.itemPanelHomeInfo.rvListAirForecast.adapter =
                                AirQualityAndWeatherForecastByHourAdapter(
                                    listHistoryForecastAirAndWeather,
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

    private fun getCurrentLocationName(
        lat: Double,
        lon: Double,
        onGetLocationName: ((String) -> Unit?)? = null
    ): String {
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
        onGetLocationName?.invoke(result)
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
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,
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
                R.string.msg_permission_gps,
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
            currentWeatherConditionViewModel.getCurrentWeatherCondition(
                lat,
                lon,
                onSuccess = {
                    if (it != null) {
                        dataCurrentWeather = it[0]
                        getForecastAndHistoryData(lat, lon)
                        isCurrentWeatherConditionLoaded = true
                    }
                },
                onError = { errorMsg ->
                    if (errorMsg != null) {
                        if (BuildConfig.DEBUG) {
                            Timber.e(errorMsg)
                        }
                    }
                }
            )

            currentAirQualityViewModel.getCurrentAirQuality(
                lat,
                lon,
                onSuccess = {
                    if (it != null) {
                        dataCurrentAirQuality = it.data[0]
                        isCurrentAirQualityLoaded = true
                    }
                },
                onError = { errorMsg ->
                    if (errorMsg != null) {
                        showToast(errorMsg)
                    }
                }
            )
        })
    }

    private fun convertWindSpeedToKmh(speedMs: Int): Int = (speedMs * 3.6).toInt()

    private fun getForecastAndHistoryData(lat: Double, lon: Double) {
        airQualityForecastAndHistoryByHourViewModel.getAirQualityForecastAndHistoryByHour(
            lat,
            lon,
            onSuccess = {
                if (it != null) {
                    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("id"))
                    val formatter = SimpleDateFormat("ha", Locale("id"))
                    var total = 0
                    it.history.forEach { historyData ->
                        val hour =
                            formatter.format(parser.parse(historyData.datetime)!!).lowercase()
                        listHistoryAndForecastAir.add(
                            AirQualityHistoryAndForecastByHour(
                                hour = hour,
                                iconAQISrc = getIconItem(historyData.aqi.toInt()),
                                aqi = historyData.aqi.toInt(),
                                pm10 = historyData.pm10.toInt(),
                                pm25 = historyData.pm25.toInt(),
                                o3 = historyData.o3.toInt(),
                                so2 = historyData.so2.toInt(),
                                no2 = historyData.no2.toInt(),
                                co = historyData.co.toInt()
                            )
                        )
                        total++
                        if (total > 2) {
                            listHistoryAndForecastAir.reverse()
                        }
                    }

                    val currentHour = formatter.format(Date()).lowercase()
                    listHistoryAndForecastAir.add(
                        AirQualityHistoryAndForecastByHour(
                            hour = currentHour,
                            iconAQISrc = getIconItem(dataCurrentAirQuality.aqi.toInt()),
                            aqi = dataCurrentAirQuality.aqi.toInt(),
                            pm10 = dataCurrentAirQuality.pm10.toInt(),
                            pm25 = dataCurrentAirQuality.pm25.toInt(),
                            o3 = dataCurrentAirQuality.o3.toInt(),
                            so2 = dataCurrentAirQuality.so2.toInt(),
                            no2 = dataCurrentAirQuality.no2.toInt(),
                            co = dataCurrentAirQuality.co.toInt()
                        )
                    )

                    total++

                    it.forecast.forEach { forecastData ->
                        val hour =
                            formatter.format(parser.parse(forecastData.datetime)!!).lowercase()
                        listHistoryAndForecastAir.add(
                            AirQualityHistoryAndForecastByHour(
                                hour = hour,
                                iconAQISrc = getIconItem(forecastData.aqi.toInt()),
                                aqi = forecastData.aqi.toInt(),
                                pm10 = forecastData.pm10.toInt(),
                                pm25 = forecastData.pm25.toInt(),
                                o3 = forecastData.o3.toInt(),
                                so2 = forecastData.so2.toInt(),
                                no2 = forecastData.no2.toInt(),
                                co = forecastData.co.toInt()
                            )
                        )
                        total++
                    }
                    isAirQualityHistoryAndForecastByHourLoaded =
                        listHistoryAndForecastAir.size == total
                }
            },
            onError = { errorMsg ->
                if (errorMsg != null) {
                    showToast(errorMsg)
                }
            }
        )

        weatherForecastAndHistoryByHourViewModel.getWeatherForecastAndHistoryByHour(
            lat,
            lon,
            onSuccess = {
                if (it != null) {
                    var total = 0
                    it.history.forEach { historyData ->
                        listForecastAndHistoryWeather.add(
                            WeatherHistoryAndForecastByHour(
                                windSpeed = historyData.windSpeed.toInt(),
                                humidity = historyData.humidity.toInt(),
                                temperature = historyData.temperature.toInt()
                            )
                        )
                        total++
                        if (total > 2) {
                            listForecastAndHistoryWeather.reverse()
                        }
                    }

                    listForecastAndHistoryWeather.add(
                        WeatherHistoryAndForecastByHour(
                            windSpeed = dataCurrentWeather.windSpeed.toInt(),
                            humidity = dataCurrentWeather.humidity.toInt(),
                            temperature = dataCurrentWeather.temperature.toInt()
                        )
                    )
                    total++

                    it.forecast.forEach { forecastData ->
                        listForecastAndHistoryWeather.add(
                            WeatherHistoryAndForecastByHour(
                                windSpeed = forecastData.windSpeed.toInt(),
                                humidity = forecastData.humidity.toInt(),
                                temperature = forecastData.temperature.toInt()
                            )
                        )
                        total++
                    }
                    isWeatherHistoryAndForecastByHourLoaded =
                        listForecastAndHistoryWeather.size == total
                }
            },
            onError = { errorMsg ->
                if (errorMsg != null) {
                    showToast(errorMsg)
                }
            }
        )
    }

    private fun setupGuide() {

        val checkPreference = CheckPreference(requireContext())
        val checkHelper = checkPreference.getCheckGuide()
        binding.scrollView.setEnableScrolling(checkHelper.isUserFinishGuide)

        if (checkHelper.isUserFinishGuide) {
            return
        }

        guide1Handler = Handler(Looper.getMainLooper())
        guide2Handler = Handler(Looper.getMainLooper())

        binding.scrollView.smoothScrollTo(0, binding.root.top)

        requireContext().showGuide(
            binding.itemLocationAndDate.root,
            "Lokasi dan tanggal saat ini"
        ) {
            requireContext().showGuide(
                binding.itemInfoAirToday.tvAQI,
                "Kualitas udara saat ini di lokasi anda"
            ) {
                requireContext().showGuide(
                    binding.itemInfoAirToday.imgLabelAir,
                    "Label status kualitas udara saat ini di lokasi anda"
                ) {
                    requireContext().showGuide(
                        binding.itemInfoAirToday.tvWindSpeed,
                        "Kecepatan udara saat ini di lokasi anda"
                    ) {
                        requireContext().showGuide(
                            binding.itemInfoAirToday.tvTemperature,
                            "Suhu saat ini di lokasi anda"
                        ) {
                            requireContext().showGuide(
                                binding.itemInfoAirToday.tvHumidity,
                                "Kelembapan saat ini di lokasi anda"
                            ) {
                                requireContext().showGuide(
                                    binding.itemPanelHomeInfo.itemStatusAirMessage.root,
                                    "Informasi kondisi udara saat ini di lokasi anda"
                                ) {
                                    binding.scrollView.smoothScrollTo(0, binding.root.bottom / 3)
                                    guide1Handler?.postDelayed({
                                        requireContext().showGuide(
                                            binding.itemPanelHomeInfo.rvListAirForecast,
                                            "Riwayat dan Prediksi Kualitas Udara, " +
                                                    "anda dapat mengklik item untuk melihat halaman detail" +
                                                    " dan menggeser untuk melihat item lainnya"
                                        ) {
                                            binding.scrollView.smoothScrollTo(
                                                0,
                                                binding.root.bottom
                                            )
                                            guide2Handler?.postDelayed({
                                                requireContext().showGuide(
                                                    binding.itemPanelHomeInfo.itemInfoListAirToday.root,
                                                    "Jenis-jenis kualitas udara"
                                                ) {
                                                    binding.scrollView.setEnableScrolling(true)
                                                    checkHelper.isUserFinishGuide = true
                                                    checkPreference.setCheckGuide(checkHelper)
                                                    binding.scrollView.smoothScrollTo(
                                                        0,
                                                        binding.root.top
                                                    )
                                                }
                                            }, DELAY_GUIDE)
                                        }
                                    }, DELAY_GUIDE)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS_MAPS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val DELAY_CALL_API: Long = 2000
        private const val DELAY_REFRESH: Long = 1000
        private const val DELAY_GUIDE: Long = 500
        private const val PERIOD_TIMER: Long = 500
        private const val TOTAL_LIST_FORECAST_AND_HISTORY_SIZE = 7
        const val FORECAST_EXTRA = "FORECAST_EXTRA"
        const val URL_EXTRA = "URL_EXTRA"
    }
}