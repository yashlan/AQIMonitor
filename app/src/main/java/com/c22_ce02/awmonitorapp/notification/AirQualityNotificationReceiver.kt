package com.c22_ce02.awmonitorapp.notification

import android.Manifest
import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.os.StrictMode
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.ui.splash.SplashActivity
import com.c22_ce02.awmonitorapp.utils.getCurrentAQIISPU
import com.c22_ce02.awmonitorapp.utils.isNetworkAvailable
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.SyncHttpClient
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import timber.log.Timber
import java.util.*


class AirQualityNotificationReceiver : BroadcastReceiver(), LocationListener {

    private lateinit var locationManager: LocationManager

    private var isLocationChanged = false
    private var newLat = 0.0
    private var newLon = 0.0

    override fun onReceive(c: Context, i: Intent?) {
        showAirQualityNotification(c)
    }

    private fun getLocation(context: Context, onGetLocation: (Double, Double) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                        return CancellationTokenSource().token
                    }

                    override fun isCancellationRequested(): Boolean {
                        return false
                    }
                }).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onGetLocation(location.latitude, location.longitude)
                } else {
                    requestLocation(context)
                    val refreshLocationTimer = Timer()
                    refreshLocationTimer.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            if (isLocationChanged) {
                                onGetLocation(newLat, newLon)
                                cancel()
                            }
                        }
                    }, 0, PERIOD)
                }
            }
        }
    }

    private fun requestLocation(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            } else {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000L,
                    1000f,
                    this
                )
            }
        }
    }

    override fun onLocationChanged(l: Location) {
        newLat = l.latitude
        newLon = l.longitude
        isLocationChanged = true
        locationManager.removeUpdates(this)
    }

    private fun setPolicy() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    private fun getCurrentLocationName(context: Context, lat: Double, lon: Double): String {
        val geocoder = Geocoder(context, Locale("id"))
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        val result = if (addresses.size > 0) {
            val adminArea =
                if (addresses[0].adminArea != null) addresses[0].adminArea else "Tidak Diketahui"
            val subLocality =
                if (addresses[0].subLocality != null) addresses[0].subLocality else adminArea
            val country = addresses[0].countryName
            "$subLocality, $country"
        } else {
            "lokasimu"
        }
        return result
    }

    private fun getCategoryName(aqi: Int): String? {
        return when (aqi) {
            in 101..150 -> "tidak sehat"
            in 151..300 -> "sangat tidak sehat"
            in 300..Int.MAX_VALUE -> "berbahaya"
            else -> null
        }
    }

    private fun getCurrentAirQuality(context: Context, onSuccess: (String, String, Int) -> Unit) {
        getLocation(context, onGetLocation = { lat, lon ->
            if (Looper.myLooper() == null) {
                Looper.prepare()
            }
            setPolicy()
            val client = SyncHttpClient()
            val url =
                "${BuildConfig.BASE_URL_WEATHERBIT}current/airquality?lat=$lat&lon=$lon&key=${BuildConfig.API_KEY_WEATHERBIT_6}"
            client.post(url, object : AsyncHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<Header?>?,
                    responseBody: ByteArray
                ) {
                    val result = String(responseBody)
                    try {
                        val locationName = getCurrentLocationName(context, lat, lon)
                        val responseObject = JSONObject(result)
                        val data = responseObject.getJSONArray("data")
                        val pm10 = data.getJSONObject(0).getString("pm10").toDouble()
                        val pm25 = data.getJSONObject(0).getString("pm25").toDouble()
                        val o3 = data.getJSONObject(0).getString("o3").toDouble()
                        val so2 = data.getJSONObject(0).getString("so2").toDouble()
                        val no2 = data.getJSONObject(0).getString("no2").toDouble()
                        val co = data.getJSONObject(0).getString("co").toDouble()

                        val aqi = getCurrentAQIISPU(
                            pm10 = pm10,
                            pm25 = pm25,
                            o3 = o3,
                            so2 = so2,
                            no2 = no2,
                            co = co
                        )

                        val title = "Indeks Kualitas Udara saat ini sebesar $aqi"
                        val message =
                            "Kualitas udara di $locationName saat ini berada di kategori ${
                                getCategoryName(aqi)
                            }, " +
                                    "jadi jangan lupa pakai masker saat keluar rumah ya!"
                        onSuccess(title, message, aqi)
                    } catch (e: Exception) {
                        Timber.e(e.message)
                    }
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header?>?,
                    responseBody: ByteArray?,
                    error: Throwable
                ) {
                    Timber.e(error.message)
                }
            })
        })
    }

    private fun showAirQualityNotification(context: Context) {

        if (!isBackgroundRunning(context)) {
            return
        }

        if (!isNetworkAvailable(context, showNotAvailableInfo = false)) {
            return
        }

        getCurrentAirQuality(context, onSuccess = { title, message, aqi ->

            if (aqi <= 100)
                return@getCurrentAirQuality

            val notifyIntent = Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                else
                    0
            )
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(notifyPendingIntent)
                .setSmallIcon(R.drawable.logo_no_background_leaf)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.logo_no_background_leaf
                    )
                )
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title)
                )
                .setAutoCancel(true)

            val notification = mBuilder.build()
            mNotificationManager.notify(ID_REPEATING, notification)
        })
    }

    fun createChannel(context: Context) {
        if (!isAlarmAlreadySet(context) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.enableLights(true)
            channel.enableVibration(true)
            channel.lockscreenVisibility = Context.MODE_PRIVATE

            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }
    }

    fun setRepeatingNotification(context: Context) {

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(context, AirQualityNotificationReceiver::class.java)

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                ID_REPEATING,
                intent,
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> PendingIntent.FLAG_IMMUTABLE
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> PendingIntent.FLAG_MUTABLE
                    else -> 0
                }
            )

        alarmManager?.cancel(pendingIntent)

        val calendar = Calendar.getInstance()
        alarmManager?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis + REPEAT_TIME,
            REPEAT_TIME,
            pendingIntent
        )
    }

    private fun isAlarmAlreadySet(context: Context): Boolean {
        val intent = Intent(context, AirQualityNotificationReceiver::class.java)
        val requestCode = ID_REPEATING

        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> PendingIntent.FLAG_IMMUTABLE
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> PendingIntent.FLAG_MUTABLE
                else -> 0
            }
        ) != null
    }

    private fun isBackgroundRunning(context: Context): Boolean {
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == context.packageName) {
                        return false
                    }
                }
            }
        }
        return true
    }

    companion object {
        private const val ID_REPEATING = 101
        private const val CHANNEL_ID = "Channel_1"
        private const val CHANNEL_NAME = "Cek Kualitas Udara"
        private const val PERIOD: Long = 500
        private const val REPEAT_TIME: Long = 60 * (60 * 1000)
    }
}