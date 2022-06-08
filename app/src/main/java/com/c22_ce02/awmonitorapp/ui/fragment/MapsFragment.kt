package com.c22_ce02.awmonitorapp.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment(R.layout.fragment_maps) {

    private lateinit var mMap : GoogleMap
    lateinit var bottomSheetDialog : BottomSheetDialog
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
        bottomSheetDialog = BottomSheetDialog()


    }

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        mMap.setOnMarkerClickListener(
            bottomSheetDialog.show(activity.supportFragmentManager(),"modal")
        )

        mapsViewModel.currentData.observe(this){
            for( (index) in it.withIndex()){
                val location = LatLng(it[index].lat, it[index].lon)
                mMap.addMarker(MarkerOptions().position(location).title(it[index].city))
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

}
