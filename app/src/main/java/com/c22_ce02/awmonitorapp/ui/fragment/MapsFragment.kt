package com.c22_ce02.awmonitorapp.ui.fragment

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.asset.DummyResponseItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MapsFragment : Fragment(){

    private var listLocation : ArrayList<DummyResponseItem> = ArrayList()
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

            for (i in 0 until locs.length() -1) {
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
                mMap.addMarker(MarkerOptions().position(location).title(name))
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
}