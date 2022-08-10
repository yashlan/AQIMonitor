package com.c22_ce02.awmonitorapp.ui.fragment

import android.Manifest
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.adapter.AirQualityAndWeatherForecastByHourAdapter
import com.c22_ce02.awmonitorapp.data.model.AirQualityAndWeatherHistoryForecastByHour
import com.c22_ce02.awmonitorapp.data.model.AirQualityHistoryAndForecastByHour
import com.c22_ce02.awmonitorapp.data.model.WeatherHistoryAndForecastByHour
import com.c22_ce02.awmonitorapp.data.preference.CheckPreference
import com.c22_ce02.awmonitorapp.data.response.CurrentAirQualityResponse
import com.c22_ce02.awmonitorapp.data.response.CurrentWeatherConditionResponse
import com.c22_ce02.awmonitorapp.databinding.FragmentHomeBinding
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
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) {

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
        if (activity != null) {

            binding.swipeRefresh.setOnRefreshListener {
                hideUI()
                refreshFragment()
                setDefaultWindowBackgroundResource()
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

            refreshUITimer?.scheduleAtFixedRate(
                object : TimerTask() {
                    override fun run() {
                        requireActivity().runOnUiThread {
                            if (isAllDataLoaded()) {
                                showUI()
                                updateUI()
                                setupGuide()
                                cancel()
                            }
                        }
                    }
                }, 0, PERIOD_TIMER
            )
        }
    }

    private fun isAllDataLoaded(): Boolean =
        isCurrentAirQualityLoaded &&
                isAirQualityHistoryAndForecastByHourLoaded &&
                isWeatherHistoryAndForecastByHourLoaded &&
                isCurrentWeatherConditionLoaded

    private fun currentAQI(): Int {
        return getCurrentAQIISPU(
            pm10 = dataCurrentAirQuality.pm10,
            pm25 = dataCurrentAirQuality.pm25,
            o3 = dataCurrentAirQuality.o3,
            so2 = dataCurrentAirQuality.so2,
            no2 = dataCurrentAirQuality.no2,
            co = dataCurrentAirQuality.co
        )
    }

    private fun resetAll(onReset: () -> Unit) {
        listHistoryForecastAirAndWeather.clear()
        listHistoryForecastAirAndWeather.removeAll(listHistoryForecastAirAndWeather.toSet())

        listForecastAndHistoryWeather.clear()
        listForecastAndHistoryWeather.removeAll(listForecastAndHistoryWeather.toSet())

        listHistoryAndForecastAir.clear()
        listHistoryAndForecastAir.removeAll(listHistoryAndForecastAir.toSet())

        if (listHistoryAndForecastAir.isEmpty() &&
            listForecastAndHistoryWeather.isEmpty() &&
            listHistoryForecastAirAndWeather.isEmpty()
        ) {
            onReset.invoke()
        }
    }

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

        val currentAQI = currentAQI()
        val itemListAirInfo = binding.itemPanelHomeInfo.itemInfoListAirToday

        changeWindowBackgroundResource(currentAQI)

        with(binding) {
            startIncrementTextAnimation(currentAQI, itemInfoAirToday.tvAQI)

            val dataW = dataCurrentWeather

            itemLocationAndDate.tvLocation.text = "Dummy Location, Mars"
            itemLocationAndDate.tvDate.text = getCurrentDate()
            itemPanelHomeInfo.itemStatusAirMessage.root.setCardBackgroundColor(
                getItemStatusAirMessageBgColor(currentAQI)
            )
            itemPanelHomeInfo.itemStatusAirMessage.tvAirStatusMsg.text =
                getAirStatusMessage(currentAQI)
            itemInfoAirToday.tvToday.text = getString(R.string.hari_ini)
            itemInfoAirToday.imgLabelAir.setImageResource(
                getAQILabelStatus(currentAQI)
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
                val pm10 = dataA.pm10.toInt()
                startIncrementTextAnimation(pm10, tvPm10)
                iconStatusPM10.setImageResource(getIconItem(pm10))
                tvStatusPM10.text = getStatusName(pm10)

                tvLabelPM25.text = spannableStringBuilder(
                    getString(R.string.pm25),
                    '2',
                    0.7f
                )
                val pm25 = dataA.pm25.toInt()
                startIncrementTextAnimation(pm25, tvPM25)
                iconStatusPM25.setImageResource(getIconItem(pm25))
                tvStatusPM25.text = getStatusName(pm25)

                tvLabelSO2.text = spannableStringBuilder(
                    getString(R.string.so2),
                    '2',
                    0.7f
                )
                val so2 = dataA.so2.toInt()
                startIncrementTextAnimation(so2, tvSO2)
                iconStatusSO2.setImageResource(getIconItem(so2))
                tvStatusSO2.text = getStatusName(so2)

                tvLabelCO.text = getString(R.string.co)
                val co = dataA.co.toInt()
                startIncrementTextAnimation(co, tvCO)
                iconStatusCO.setImageResource(getIconItem(co))
                tvStatusCO.text = getStatusName(co)

                tvLabelNO2.text = spannableStringBuilder(
                    getString(R.string.no2),
                    '2',
                    0.7f
                )
                val no2 = dataA.no2.toInt()
                startIncrementTextAnimation(no2, tvNO2)
                iconStatusNO2.setImageResource(getIconItem(no2))
                tvStatusNO2.text = getStatusName(no2)

                tvLabelO3.text = spannableStringBuilder(
                    getString(R.string.o3),
                    '3',
                    0.7f
                )
                val o3 = dataA.o3.toInt()
                startIncrementTextAnimation(o3, tvO3)
                iconStatusO3.setImageResource(getIconItem(o3))
                tvStatusO3.text = getStatusName(o3)
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

    private fun convertWindSpeedToKmh(speedMs: Int): Int = (speedMs * 3.6).toInt()

    private fun loadAllData() {
        currentWeatherConditionViewModel.getCurrentWeatherCondition(
            onSuccess = {
                if (it != null) {
                    dataCurrentWeather = it[0]
                    isCurrentWeatherConditionLoaded = true
                }
            }
        )

        currentAirQualityViewModel.getCurrentAirQuality(
            onSuccess = {
                if (it != null) {
                    dataCurrentAirQuality = it.data[0]
                    isCurrentAirQualityLoaded = true
                }
            }
        )

        getForecastAndHistoryData()
    }

    private fun getForecastAndHistoryData() {

        airQualityForecastAndHistoryByHourViewModel.getAirQualityForecastAndHistoryByHour(
            onSuccess = {
                if (it != null) {
                    var total = 0
                    it.history.forEach { historyData ->
                        listHistoryAndForecastAir.add(
                            AirQualityHistoryAndForecastByHour(
                                hour = historyData.datetime,
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
                    }

                    val currentAQI = currentAQI()
                    val currentHour =
                        SimpleDateFormat("ha", Locale("id")).format(Date()).lowercase()
                    listHistoryAndForecastAir.add(
                        AirQualityHistoryAndForecastByHour(
                            hour = currentHour,
                            iconAQISrc = getIconItem(currentAQI),
                            aqi = currentAQI,
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
                        listHistoryAndForecastAir.add(
                            AirQualityHistoryAndForecastByHour(
                                hour = forecastData.datetime,
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
        )

        weatherForecastAndHistoryByHourViewModel.getWeatherForecastAndHistoryByHour(
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