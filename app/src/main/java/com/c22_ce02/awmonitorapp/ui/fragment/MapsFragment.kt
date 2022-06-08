package com.c22_ce02.awmonitorapp.ui.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.FragmentMapsBinding
import com.c22_ce02.awmonitorapp.ui.view.model.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment(R.layout.fragment_maps) {

    private lateinit var mMap : GoogleMap
    private val mapsViewModel: MapsViewModel by viewModels()
    private val binding by viewBinding(FragmentMapsBinding::bind, onViewDestroyed = {
        stopHandler()
    })
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapsViewModel.getCurrentData()

        mapsViewModel.showLoading.observe(this){
            showLoading(it)
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        mapsViewModel.currentData.observe(this){
            for( (index) in it.withIndex()){
                val location = LatLng(it[index].lat, it[index].lon)

                when{
                    it[index].aqi < 50 -> {
                        mMap.addMarker(
                            MarkerOptions().position(location).title("${it[index].city} - Baik")
                                .snippet("AQI: ${it[index].aqi}, o3: ${it[index].o3}, PM10: ${it[index].pm10}, PM25: ${it[index].pm25}, S02: ${it[index].so2}, CO: ${it[index].co}")
                                .icon(
                                    vectorToBitmap(
                                        R.drawable.ic_baseline_location_on_24,
                                        Color.parseColor("#32C090")
                                    )
                                )
                        )
                    }

                    it[index].aqi < 100 -> {
                        mMap.addMarker(
                            MarkerOptions().position(location).title("${it[index].city} - Sedang")
                                .snippet("AQI: ${it[index].aqi}, o3: ${it[index].o3}, PM10: ${it[index].pm10}, PM25: ${it[index].pm25}, S02: ${it[index].so2}, CO: ${it[index].co}")
                                .icon(
                                    vectorToBitmap(
                                        R.drawable.ic_baseline_location_on_24,
                                        Color.parseColor("#004CE8")
                                    )
                                )
                        )
                    }

                    it[index].aqi < 150 -> {
                        mMap.addMarker(
                            MarkerOptions().position(location).title("${it[index].city} - Tidak Sehat")
                                .snippet("AQI: ${it[index].aqi}, o3: ${it[index].o3}, PM10: ${it[index].pm10}, PM25: ${it[index].pm25}, S02: ${it[index].so2}, CO: ${it[index].co}")
                                .icon(
                                    vectorToBitmap(
                                        R.drawable.ic_baseline_location_on_24,
                                        Color.parseColor("#FFE37E")
                                    )
                                )
                        )
                    }

                    it[index].aqi < 200 -> {
                        mMap.addMarker(
                            MarkerOptions().position(location).title("${it[index].city} - Sangat Tidak Sehat")
                                .snippet("AQI: ${it[index].aqi}, o3: ${it[index].o3}, PM10: ${it[index].pm10}, PM25: ${it[index].pm25}, S02: ${it[index].so2}, CO: ${it[index].co}")
                                .icon(
                                    vectorToBitmap(
                                        R.drawable.ic_baseline_location_on_24,
                                        Color.parseColor("#FF3333")
                                    )
                                )
                        )
                    }

                    it[index].aqi < 300 -> {
                        mMap.addMarker(
                            MarkerOptions().position(location).title("${it[index].city} - Berbahaya!")
                                .snippet("AQI: ${it[index].aqi}, o3: ${it[index].o3}, PM10: ${it[index].pm10}, PM25: ${it[index].pm25}, S02: ${it[index].so2}, CO: ${it[index].co}")
                                .icon(
                                    vectorToBitmap(
                                        R.drawable.ic_baseline_location_on_24,
                                        Color.parseColor("#181A20")
                                    )
                                )
                        )
                    }

                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location))

            }
        }
    }

    private fun stopHandler() {
        handler.removeCallbacksAndMessages(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.mapsLoading.visibility = View.VISIBLE
            binding.map.visibility = View.GONE
        } else {
            binding.mapsLoading.visibility = View.GONE
            binding.map.visibility = View.VISIBLE
        }
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