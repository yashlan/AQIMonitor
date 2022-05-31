package com.c22_ce02.awmonitorapp.notification

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
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
import com.c22_ce02.awmonitorapp.utils.isNetworkAvailable
import com.google.android.gms.location.LocationServices
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.SyncHttpClient
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.thread


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
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onGetLocation(location.latitude, location.longitude)
                } else {
                    requestLocation(context)
                    val refreshLocationTimer = Timer()
                    refreshLocationTimer.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            thread {
                                if (isLocationChanged) {
                                    onGetLocation(newLat, newLon)
                                    cancel()
                                }
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

    private fun getCurrentAirQuality(context: Context, onSuccess: (String, String) -> Unit) {
        getLocation(context, onGetLocation = { lat, lon ->
            if (Looper.myLooper() == null) {
                Looper.prepare()
            }
            setPolicy()
            val client = SyncHttpClient()
            val url =
                "${BuildConfig.BASE_URL_WEATHERBIT}current/airquality?lat=$lat&lon=$lon&key=${BuildConfig.API_KEY_WEATHERBIT}"
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
                        val aqi = data.getJSONObject(0).getString("aqi")
                        val title = "Kualitas Udara Indeks Saat Ini $aqi"
                        val message =
                            "kualitas udara di $locationName saat ini sedang tidak sehat, " +
                                    "jangan lupa pakai masker ketika keluar rumah"
                        onSuccess(title, message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header?>?,
                    responseBody: ByteArray?,
                    error: Throwable
                ) {

                }
            })
        })
    }

    private fun showAirQualityNotification(context: Context) {

        if (!isNetworkAvailable(context, showNotAvailableInfo = false))
            return

        getCurrentAirQuality(context, onSuccess = { title, message ->
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.ic_notifications_black_24dp
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

    fun createChannel(
        context: Context
    ) {
        if (!isAlarmAlreadySet(context) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    PendingIntent.FLAG_IMMUTABLE
                else
                    0
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                PendingIntent.FLAG_NO_CREATE
            else
                0
        ) != null
    }

    companion object {
        private const val ID_REPEATING = 101
        private const val CHANNEL_ID = "Channel_1"
        private const val CHANNEL_NAME = "Cek Kualitas Udara"
        private const val PERIOD: Long = 500
        private const val REPEAT_TIME: Long = 60 * (60 * 1000)
    }

}