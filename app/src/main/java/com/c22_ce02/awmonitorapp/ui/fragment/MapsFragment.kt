package com.c22_ce02.awmonitorapp.ui.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.asset.DummyResponseItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MapsFragment : Fragment() {

    private var listLocation: ArrayList<DummyResponseItem> = ArrayList()
    private lateinit var mMap: GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->
        getListCity(googleMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    private fun getListCity(googleMap: GoogleMap) {
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
                                Color.parseColor("#1592FF")
                            )
                        )
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location))


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun loadJSONFromAsset(): String? {
        val json = try {
            val inputStream = requireContext().assets.open("city.json")
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

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
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