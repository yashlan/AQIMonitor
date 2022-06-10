package com.c22_ce02.awmonitorapp.ui.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.data.model.AirQualityMaps
import com.c22_ce02.awmonitorapp.databinding.FragmentMapsBinding
import com.c22_ce02.awmonitorapp.ui.view.model.MapsViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.MapsViewModelFactory
import com.c22_ce02.awmonitorapp.utils.showToast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog


class MapsFragment : Fragment(R.layout.fragment_maps) {

    private lateinit var mMap: GoogleMap
    private val mapsViewModel: MapsViewModel by viewModels {
        MapsViewModelFactory()
    }
    private var callApiHandler: Handler? = null
    private val binding by viewBinding(FragmentMapsBinding::bind, onViewDestroyed = {
        callApiHandler?.removeCallbacksAndMessages(null)
    })
    private var listMarkerMaps: List<MutableMap<Marker?, String?>> = listOf(HashMap())
    private var listDataAirQuality: List<MutableMap<String?, AirQualityMaps?>> = listOf(HashMap())
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        showLoading(true)
        callApiHandler = Handler(Looper.getMainLooper())
        callApiHandler?.postDelayed({
            getCurrentAirQuality34Province()
        }, DELAY_CALL_API)
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

    private fun showBottomSheetDialogMaps(
        item: MutableMap<String?, AirQualityMaps?>,
        idItem: String?,
    ) {
        val sheet = BottomSheetDialog(requireContext())
        sheet.setContentView(R.layout.bottom_sheet_layout)

        val tvCity = sheet.findViewById<TextView>(R.id.tv_city)
        val tvAqi = sheet.findViewById<TextView>(R.id.tv_aqi_bottom)
        val tvPm10 = sheet.findViewById<TextView>(R.id.tv_pm10)
        val tvPm25 = sheet.findViewById<TextView>(R.id.tv_pm25)
        val tvNo2 = sheet.findViewById<TextView>(R.id.tv_no2)
        val tvO3 = sheet.findViewById<TextView>(R.id.tv_o3)
        val tvSo2 = sheet.findViewById<TextView>(R.id.tv_so2)
        val tvCo = sheet.findViewById<TextView>(R.id.tv_co)

        val city = item[idItem]?.city
        val aqi = item[idItem]?.aqi?.toInt().toString()
        val pm10 = item[idItem]?.pm10?.toInt().toString()
        val pm25 = item[idItem]?.pm25?.toInt().toString()
        val no2 = item[idItem]?.no2?.toInt().toString()
        val o3 = item[idItem]?.o3?.toInt().toString()
        val so2 = item[idItem]?.so2?.toInt().toString()
        val co = item[idItem]?.co?.toInt().toString()

        tvCity?.text = city
        tvAqi?.text = aqi
        tvPm10?.text = pm10
        tvPm25?.text = pm25
        tvNo2?.text = no2
        tvO3?.text = o3
        tvSo2?.text = so2
        tvCo?.text = co

        if (!sheet.isShowing) {
            sheet.show()
        }
    }

    private fun getMarkerBitmapFromView(aqi: Int): BitmapDescriptor {
        val customMarkerView = View.inflate(requireContext(), R.layout.view_custom_marker, null)
        val markerTvAQI = customMarkerView?.findViewById<View>(R.id.tvAQI) as TextView
        markerTvAQI.text = aqi.toString()
        customMarkerView.rootView.setBackgroundResource(
            when (aqi) {
                in 0..50 -> R.drawable.ic_marker_baik
                in 51..100 -> R.drawable.ic_marker_sedang
                in 101..150 -> R.drawable.ic_marker_tidak_sehat
                in 151..300 -> R.drawable.ic_marker_sanget_tidak_sehat
                else -> R.drawable.ic_marker_berbahaya
            }
        )
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(
            0,
            0,
            customMarkerView.measuredWidth,
            customMarkerView.measuredHeight
        )
        val returnedBitmap = Bitmap.createBitmap(
            customMarkerView.width,
            customMarkerView.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = customMarkerView.background
        drawable?.draw(canvas)
        customMarkerView.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(returnedBitmap)
    }

    private fun getCurrentAirQuality34Province() {
        mapsViewModel.getCurrentAirQuality34Province(
            onSuccess = { list ->
                showLoading(false)
                list?.forEach { item ->
                    val location = LatLng(item.lat, item.lon)
                    val city = item.city
                    val aqi = item.aqi.toInt()
                    listDataAirQuality.forEach { dataAir ->

                        dataAir[city] = AirQualityMaps(
                            aqi = item.aqi,
                            pm10 = item.pm10,
                            pm25 = item.pm25,
                            no2 = item.no2,
                            o3 = item.o3,
                            so2 = item.so2,
                            co = item.co,
                            city = city
                        )

                        val marker = mMap.addMarker(
                            MarkerOptions()
                                .title(city)
                                .position(location)
                                .icon(getMarkerBitmapFromView(aqi))
                        )

                        listMarkerMaps.forEach { map ->
                            map[marker] = marker?.title
                            mMap.setOnMarkerClickListener {
                                if (it.title.equals(map[it], ignoreCase = true)) {
                                    showBottomSheetDialogMaps(dataAir, map[it])
                                }
                                false
                            }
                        }
                    }

                    val firstCameraPos = LatLng(-1.845549, 120.513968)
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(5.5f))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(firstCameraPos))
                }
            },
            onError = { errorMsg ->
                showLoading(false)
                if (errorMsg != null) {
                    showToast(errorMsg)
                }
            }
        )
    }
    companion object {
        private const val DELAY_CALL_API: Long = 2000
    }
}