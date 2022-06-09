package com.c22_ce02.awmonitorapp.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
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
import com.google.android.material.bottomsheet.BottomSheetDialog


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

    @SuppressLint("InflateParams")
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        mapsViewModel.currentData.observe(this){ current ->

            for( (index) in current.withIndex()){
                val location = LatLng(current[index].lat, current[index].lon)

                when{

                    current[index].aqi < 50 -> {
                        mMap.addMarker(
                            MarkerOptions().position(location).title("${current[index].city} - Baik")
                                .snippet("AQI: ${current[index].aqi}, o3: ${current[index].o3}, PM10: ${current[index].pm10}, PM25: ${current[index].pm25}, S02: ${current[index].so2}, CO: ${current[index].co}")
                                .icon(
                                    vectorToBitmap(
                                        R.drawable.ic_baseline_location_on_24,
                                        Color.parseColor("#32C090")
                                    )
                                ).rotation(index.toFloat())
                        )

                        mMap.setOnInfoWindowClickListener {
                            val dialog = BottomSheetDialog(requireContext())
                            val view = layoutInflater.inflate(R.layout.bottom_sheet_layout,null)



                            // deklarasi komponen view
                            val tvCity = view.findViewById<TextView>(R.id.tv_city)
                            val tvAqi = view.findViewById<TextView>(R.id.tv_aqi_bottom)
                            val tvPm10 = view.findViewById<TextView>(R.id.tv_pm10)
                            val tvPm25 = view.findViewById<TextView>(R.id.tv_pm25)
                            val tvNo2 = view.findViewById<TextView>(R.id.tv_no2)
                            val tvO3 = view.findViewById<TextView>(R.id.tv_o3)
                            val tvSo2 = view.findViewById<TextView>(R.id.tv_so2)
                            val tvCo = view.findViewById<TextView>(R.id.tv_co)

                            // get title index > diakalin biar dapet index
                            val q = it.rotation.toInt()
                            tvCity.text = it.title
                            tvAqi.text = "${current[q].aqi}"
                            tvPm10.text = "${current[q].pm10}"
                            tvPm25.text = "${current[q].pm25}"
                            tvNo2.text = "${current[q].no2}"
                            tvO3.text = "${current[q].o3}"
                            tvSo2.text = "${current[q].so2}"
                            tvCo.text = "${current[q].co}"

                            dialog.setContentView(view)
                            dialog.show()
                        }

                    }

                    current[index].aqi < 100 -> {
                        mMap.addMarker(
                            MarkerOptions().position(location).title("${current[index].city} - Sedang")
                                .snippet("AQI: ${current[index].aqi}, o3: ${current[index].o3}, PM10: ${current[index].pm10}, PM25: ${current[index].pm25}, S02: ${current[index].so2}, CO: ${current[index].co}")
                                .icon(
                                    vectorToBitmap(
                                        R.drawable.ic_baseline_location_on_24,
                                        Color.parseColor("#004CE8")
                                    )
                                )
                        )

                        mMap.setOnInfoWindowClickListener {
                            val dialog = BottomSheetDialog(requireContext())
                            val view = layoutInflater.inflate(R.layout.bottom_sheet_layout,null)



                            // deklarasi komponen view
                            val tvCity = view.findViewById<TextView>(R.id.tv_city)
                            val tvAqi = view.findViewById<TextView>(R.id.tv_aqi_bottom)
                            val tvPm10 = view.findViewById<TextView>(R.id.tv_pm10)
                            val tvPm25 = view.findViewById<TextView>(R.id.tv_pm25)
                            val tvNo2 = view.findViewById<TextView>(R.id.tv_no2)
                            val tvO3 = view.findViewById<TextView>(R.id.tv_o3)
                            val tvSo2 = view.findViewById<TextView>(R.id.tv_so2)
                            val tvCo = view.findViewById<TextView>(R.id.tv_co)


                            val q = it.rotation.toInt()
                            tvCity.text = it.title
                            tvAqi.text = "${current[q].aqi}"
                            tvPm10.text = "${current[q].pm10}"
                            tvPm25.text = "${current[q].pm25}"
                            tvNo2.text = "${current[q].no2}"
                            tvO3.text = "${current[q].o3}"
                            tvSo2.text = "${current[q].so2}"
                            tvCo.text = "${current[q].co}"

                            dialog.setContentView(view)
                            dialog.show()
                        }
                    }

                    current[index].aqi < 150 -> {
                        mMap.addMarker(
                            MarkerOptions().position(location).title("${current[index].city} - Tidak Sehat")
                                .snippet("AQI: ${current[index].aqi}, o3: ${current[index].o3}, PM10: ${current[index].pm10}, PM25: ${current[index].pm25}, S02: ${current[index].so2}, CO: ${current[index].co}")
                                .icon(
                                    vectorToBitmap(
                                        R.drawable.ic_baseline_location_on_24,
                                        Color.parseColor("#FFE37E")
                                    )
                                )
                        )

                        mMap.setOnInfoWindowClickListener {
                            val dialog = BottomSheetDialog(requireContext())
                            val view = layoutInflater.inflate(R.layout.bottom_sheet_layout,null)



                            // deklarasi komponen view
                            val tvCity = view.findViewById<TextView>(R.id.tv_city)
                            val tvAqi = view.findViewById<TextView>(R.id.tv_aqi_bottom)
                            val tvPm10 = view.findViewById<TextView>(R.id.tv_pm10)
                            val tvPm25 = view.findViewById<TextView>(R.id.tv_pm25)
                            val tvNo2 = view.findViewById<TextView>(R.id.tv_no2)
                            val tvO3 = view.findViewById<TextView>(R.id.tv_o3)
                            val tvSo2 = view.findViewById<TextView>(R.id.tv_so2)
                            val tvCo = view.findViewById<TextView>(R.id.tv_co)

                            val q = it.rotation.toInt()
                            tvCity.text = it.title
                            tvAqi.text = "${current[q].aqi}"
                            tvPm10.text = "${current[q].pm10}"
                            tvPm25.text = "${current[q].pm25}"
                            tvNo2.text = "${current[q].no2}"
                            tvO3.text = "${current[q].o3}"
                            tvSo2.text = "${current[q].so2}"
                            tvCo.text = "${current[q].co}"

                            dialog.setContentView(view)
                            dialog.show()
                        }
                    }

                    current[index].aqi < 200 -> {
                        mMap.addMarker(
                            MarkerOptions().position(location).title("${current[index].city} - Sangat Tidak Sehat")
                                .snippet("AQI: ${current[index].aqi}, o3: ${current[index].o3}, PM10: ${current[index].pm10}, PM25: ${current[index].pm25}, S02: ${current[index].so2}, CO: ${current[index].co}")
                                .icon(
                                    vectorToBitmap(
                                        R.drawable.ic_baseline_location_on_24,
                                        Color.parseColor("#FF3333")
                                    )
                                )
                        )

                        mMap.setOnInfoWindowClickListener {
                            val dialog = BottomSheetDialog(requireContext())
                            val view = layoutInflater.inflate(R.layout.bottom_sheet_layout,null)

                            // deklarasi komponen view
                            val tvCity = view.findViewById<TextView>(R.id.tv_city)
                            val tvAqi = view.findViewById<TextView>(R.id.tv_aqi_bottom)
                            val tvPm10 = view.findViewById<TextView>(R.id.tv_pm10)
                            val tvPm25 = view.findViewById<TextView>(R.id.tv_pm25)
                            val tvNo2 = view.findViewById<TextView>(R.id.tv_no2)
                            val tvO3 = view.findViewById<TextView>(R.id.tv_o3)
                            val tvSo2 = view.findViewById<TextView>(R.id.tv_so2)
                            val tvCo = view.findViewById<TextView>(R.id.tv_co)


                            val q = it.rotation.toInt()
                            tvCity.text = it.title
                            tvAqi.text = "${current[q].aqi}"
                            tvPm10.text = "${current[q].pm10}"
                            tvPm25.text = "${current[q].pm25}"
                            tvNo2.text = "${current[q].no2}"
                            tvO3.text = "${current[q].o3}"
                            tvSo2.text = "${current[q].so2}"
                            tvCo.text = "${current[q].co}"

                            dialog.setContentView(view)
                            dialog.show()
                        }
                    }

                    current[index].aqi < 300 -> {
                        mMap.addMarker(
                            MarkerOptions().position(location).title("${current[index].city} - Berbahaya!")
                                .snippet("AQI: ${current[index].aqi}, o3: ${current[index].o3}, PM10: ${current[index].pm10}, PM25: ${current[index].pm25}, S02: ${current[index].so2}, CO: ${current[index].co}")
                                .icon(
                                    vectorToBitmap(
                                        R.drawable.ic_baseline_location_on_24,
                                        Color.parseColor("#181A20")
                                    )
                                )
                        )

                        mMap.setOnInfoWindowClickListener {
                            val dialog = BottomSheetDialog(requireContext())
                            val view = layoutInflater.inflate(R.layout.bottom_sheet_layout,null)



                            // deklarasi komponen view
                            val tvCity = view.findViewById<TextView>(R.id.tv_city)
                            val tvAqi = view.findViewById<TextView>(R.id.tv_aqi_bottom)
                            val tvPm10 = view.findViewById<TextView>(R.id.tv_pm10)
                            val tvPm25 = view.findViewById<TextView>(R.id.tv_pm25)
                            val tvNo2 = view.findViewById<TextView>(R.id.tv_no2)
                            val tvO3 = view.findViewById<TextView>(R.id.tv_o3)
                            val tvSo2 = view.findViewById<TextView>(R.id.tv_so2)
                            val tvCo = view.findViewById<TextView>(R.id.tv_co)


                            val q = it.rotation.toInt()
                            tvCity.text = it.title
                            tvAqi.text = "${current[q].aqi}"
                            tvPm10.text = "${current[q].pm10}"
                            tvPm25.text = "${current[q].pm25}"
                            tvNo2.text = "${current[q].no2}"
                            tvO3.text = "${current[q].o3}"
                            tvSo2.text = "${current[q].so2}"
                            tvCo.text = "${current[q].co}"

                            dialog.setContentView(view)
                            dialog.show()
                        }
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