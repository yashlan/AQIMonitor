package com.c22_ce02.awmonitorapp.ui.view.model

import android.annotation.SuppressLint
import android.app.Application
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.asset.DummyResponseItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MapsViewModel(application: Application): AndroidViewModel(application) {

    private var listLocation: ArrayList<DummyResponseItem> = ArrayList()
    private lateinit var mMap: GoogleMap


    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext


     fun getListCity(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            val jsonObject = JSONObject(loadJSONFromAsset()!!)
            val locs = jsonObject.getJSONArray("data")

            for (i in 0 until locs.length() - 1) {
                val loc = locs.getJSONObject(i)
                val data = DummyResponseItem()

                data.lat = loc.getString("lat")
                data.lng = loc.getString("lng")
                data.city = loc.getString("city")

                listLocation.add(data)

                val location = LatLng(
                    listLocation[i].lat!!.toDouble(),
                    listLocation[i].lng?.toDouble()!!
                )

                val name = listLocation[i].city.toString()


                mMap.addMarker(
                    MarkerOptions().position(location).title(name)
                        .snippet("PM2.5: 47, PM10: 15, AQI: 14")
                        .icon(
                            vectorToBitmap(
                                R.drawable.ic_marker_green,
                                Color.parseColor("#FF3333")
                            )
                        )
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


     fun loadJSONFromAsset(): String? {
        val json = try {
            val inputStream = context.assets.open("city.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

     fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(context.resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }



}