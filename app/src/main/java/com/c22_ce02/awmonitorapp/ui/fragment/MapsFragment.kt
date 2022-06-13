package com.c22_ce02.awmonitorapp.ui.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.data.model.AirQualityMaps
import com.c22_ce02.awmonitorapp.data.preference.CheckPreference
import com.c22_ce02.awmonitorapp.data.response.CurrentAirQuality34ProvinceResponse
import com.c22_ce02.awmonitorapp.databinding.FragmentMapsBinding
import com.c22_ce02.awmonitorapp.ui.view.model.MapsViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.MapsViewModelFactory
import com.c22_ce02.awmonitorapp.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MapsFragment : Fragment(R.layout.fragment_maps) {

    private lateinit var mMap: GoogleMap
    private val mapsViewModel: MapsViewModel by viewModels {
        MapsViewModelFactory()
    }
    private var callApiHandler: Handler? = null
    private val binding by viewBinding(FragmentMapsBinding::bind, onViewDestroyed = {
        callApiHandler?.removeCallbacksAndMessages(null)
    })
    private val listDataCurrentLocal = ArrayList<CurrentAirQuality34ProvinceResponse.CurrentItem>()
    private var listMarkerMaps: List<MutableMap<Marker?, String?>> = listOf(HashMap())
    private var listDataAirQuality: List<MutableMap<String?, AirQualityMaps?>> = listOf(HashMap())
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        showLoading(true)
        callApiHandler = Handler(Looper.getMainLooper())
        callApiHandler?.postDelayed({
            requireActivity().runOnUiThread {
                getCurrentAirQuality34Province()
            }
        }, DELAY_CALL_API)
    }

    override fun onDestroy() {
        super.onDestroy()
        callApiHandler?.removeCallbacksAndMessages(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isNetworkAvailable(requireContext(), true)) {
            return
        }

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

    private fun getCardViewBackgroundColor(param: Int): Int {
        return ActivityCompat.getColor(
            requireContext(),
            when (param) {
                in 0..50 -> R.color.warna_baik
                in 51..100 -> R.color.warna_sedang
                in 101..150 -> R.color.warna_tidak_sehat
                in 151..300 -> R.color.warna_sangat_tidak_sehat
                else -> R.color.warna_berbahaya
            }
        )
    }

    private fun showBottomSheetDialogMaps(
        item: MutableMap<String?, AirQualityMaps?>,
        idItem: String?,
    ) {
        val sheet = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)
        sheet.setContentView(R.layout.bottom_sheet_layout)

        val tvPm10Title = sheet.findViewById<TextView>(R.id.tvPm10Title)
        val tvPm25Title = sheet.findViewById<TextView>(R.id.tvPm25Title)
        val tvNo2Title = sheet.findViewById<TextView>(R.id.tvNo2Title)
        val tvO3Title = sheet.findViewById<TextView>(R.id.tvO3Title)
        val tvSo2Title = sheet.findViewById<TextView>(R.id.tvSo2Title)

        tvPm10Title?.text = spannableStringBuilder(getString(R.string.pm10), '1', 0.7f)
        tvPm25Title?.text = spannableStringBuilder(getString(R.string.pm25), '2', 0.7f)
        tvNo2Title?.text = spannableStringBuilder(getString(R.string.no2), '2', 0.7f)
        tvO3Title?.text = spannableStringBuilder(getString(R.string.o3), '3', 0.7f)
        tvSo2Title?.text = spannableStringBuilder(getString(R.string.so2), '2', 0.7f)

        val tvCity = sheet.findViewById<TextView>(R.id.tv_city)
        val tvAqi = sheet.findViewById<TextView>(R.id.tvAQI)
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

        val imgLabelAir = sheet.findViewById<ImageView>(R.id.imgLabelAirSheet)

        imgLabelAir?.setBackgroundResource(
            when (aqi.toInt()) {
                in 0..50 -> R.drawable.ic_label_sheet_baik
                in 51..100 -> R.drawable.ic_label_sheet_sedang
                in 101..150 -> R.drawable.ic_label_sheet_tidak_sehat
                in 151..300 -> R.drawable.ic_label_sheet_sangat_tidak_sehat
                else -> R.drawable.ic_label_sheet_berbahaya
            }
        )

        val cardAQI = sheet.findViewById<CardView>(R.id.cardSheetAQI)
        val cardPM10 = sheet.findViewById<CardView>(R.id.cardSheetPM10)
        val cardPM25 = sheet.findViewById<CardView>(R.id.cardSheetPM25)
        val cardNO2 = sheet.findViewById<CardView>(R.id.cardSheetNO2)
        val cardO3 = sheet.findViewById<CardView>(R.id.cardSheetO3)
        val cardSO2 = sheet.findViewById<CardView>(R.id.cardSheetSO2)
        val cardCO = sheet.findViewById<CardView>(R.id.cardSheetCO)

        cardAQI?.setCardBackgroundColor(getCardViewBackgroundColor(aqi.toInt()))
        cardPM10?.setCardBackgroundColor(getCardViewBackgroundColor(pm10.toInt()))
        cardPM25?.setCardBackgroundColor(getCardViewBackgroundColor(pm25.toInt()))
        cardNO2?.setCardBackgroundColor(getCardViewBackgroundColor(no2.toInt()))
        cardO3?.setCardBackgroundColor(getCardViewBackgroundColor(o3.toInt()))
        cardSO2?.setCardBackgroundColor(getCardViewBackgroundColor(so2.toInt()))
        cardCO?.setCardBackgroundColor(getCardViewBackgroundColor(co.toInt()))

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
        val isFilePresent = isFilePresent(requireContext())
        if (isFilePresent) {
/*            if (BuildConfig.DEBUG) {
                showToastInThread("file sudah ada di storage, load via local")
            }*/
            val mapsJson = readResponse(requireContext())
            mapsJson?.let { getListDataLocal(it) }
            return
        }

        mapsViewModel.getCurrentAirQuality34Province(
            onSuccess = { json ->
                val isFileCreated = saveResponse(requireContext(), json)
                if (isFileCreated) {
/*                    if (BuildConfig.DEBUG) {
                        showToastInThread("file tersimpan di storagemu!!")
                    }*/
                    val mapsJson = readResponse(requireContext())
                    mapsJson?.let { getListDataLocal(it) }
                } else {
                    showSnackBar(
                        binding.root,
                        R.string.errorMsg,
                        R.string.yes,
                        onClickOkAction = {
                            val navHostFragment =
                                requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home_activity)
                            navHostFragment?.findNavController()?.navigate(R.id.navigation_maps)
                        }
                    )
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

    private fun getListDataLocal(json: String) {
        showLoading(false)
        try {
            val jsonObject = JSONObject(json)
            val data = jsonObject.getJSONObject("data")
            val current = data.getJSONArray("current")

            for (i in 0 until current.length()) {
                val obj = current.getJSONObject(i)

                val aqi = obj.getString("aqi")
                val city = obj.getString("city")
                val co = obj.getString("co")
                val lat = obj.getString("lat")
                val lon = obj.getString("lon")
                val no2 = obj.getString("no2")
                val o3 = obj.getString("o3")
                val pm10 = obj.getString("pm10")
                val pm25 = obj.getString("pm25")
                val so2 = obj.getString("so2")

                val item = CurrentAirQuality34ProvinceResponse.CurrentItem(
                    aqi = aqi.toDouble(),
                    city = city,
                    co = co.toDouble(),
                    lat = lat.toDouble(),
                    lon = lon.toDouble(),
                    no2 = no2.toDouble(),
                    o3 = o3.toDouble(),
                    pm10 = pm10.toDouble(),
                    pm25 = pm25.toDouble(),
                    so2 = so2.toDouble(),
                )
                listDataCurrentLocal.add(item)

                listDataCurrentLocal.forEach {
                    val location = LatLng(it.lat, it.lon)
                    listDataAirQuality.forEach { dataAir ->
                        dataAir[city] = AirQualityMaps(
                            aqi = it.aqi,
                            pm10 = it.pm10,
                            pm25 = it.pm25,
                            no2 = it.no2,
                            o3 = it.o3,
                            so2 = it.so2,
                            co = it.co,
                            city = it.city
                        )

                        val marker = mMap.addMarker(
                            MarkerOptions()
                                .position(location)
                                .icon(getMarkerBitmapFromView(it.aqi.toInt()))
                        )

                        marker?.tag = city

                        listMarkerMaps.forEach { map ->
                            map[marker] = marker?.tag as String
                            mMap.setOnMarkerClickListener { m ->
                                val tag = m.tag as String
                                if (tag.equals(map[m], ignoreCase = true)) {
                                    showBottomSheetDialogMaps(dataAir, map[m])
                                }
                                false
                            }

                            val firstCameraPos = LatLng(-1.845549, 120.513968)
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(5.5f))
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(firstCameraPos))
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val DELAY_CALL_API: Long = 2000
    }
}