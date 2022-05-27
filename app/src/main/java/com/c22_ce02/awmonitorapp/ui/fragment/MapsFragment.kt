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
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment


class MapsFragment : Fragment(R.layout.fragment_maps) {


    private val mapsViewModel: MapsViewModel by viewModels()
    private val binding by viewBinding(FragmentMapsBinding::bind)


    private val callback = OnMapReadyCallback { googleMap ->
        showLoading(true)
        mapsViewModel.getListCity(googleMap)
        Handler(Looper.getMainLooper()).postDelayed({
            showLoading(false)
        }, MAPS_FAKE_TIME_LOAD)
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

    companion object {
        private const val MAPS_FAKE_TIME_LOAD: Long = 8000
    }

}