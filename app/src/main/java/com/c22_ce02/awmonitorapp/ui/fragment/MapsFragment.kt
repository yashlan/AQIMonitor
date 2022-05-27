package com.c22_ce02.awmonitorapp.ui.fragment

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.FragmentMapsBinding
import com.c22_ce02.awmonitorapp.ui.view.model.MapsViewModel
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment


class MapsFragment : Fragment(R.layout.fragment_maps) {


    private val mapsViewModel : MapsViewModel by viewModels()
    private lateinit var binding : FragmentMapsBinding


    private val callback = OnMapReadyCallback { googleMap ->
        showLoading(true)
        mapsViewModel.getListCity(googleMap)
        Handler(Looper.getMainLooper()).postDelayed({
            showLoading(false)
        }, MAPS_FAKE_TIME_LOAD)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.mapsLoading.visibility = View.VISIBLE
        } else {
            binding.mapsLoading.visibility = View.GONE
        }
    }

    companion object {
        private const val MAPS_FAKE_TIME_LOAD: Long = 8000
    }

}