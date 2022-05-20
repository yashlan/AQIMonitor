package com.c22_ce02.awmonitorapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.asset.DummyResponseItem
import com.c22_ce02.awmonitorapp.databinding.ActivityDiscoverBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class DiscoverActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDiscoverBinding
    private var listLocation : ArrayList<DummyResponseItem> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiscoverBinding.inflate(layoutInflater)
        setContentView(binding.root)


            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        getListCity(googleMap)
    }

    private fun getListCity(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            val jsonObject = JSONObject(loadJSONFromAsset()!!)
            val locs = jsonObject.getJSONArray("data")

            for (i in 0..locs.length()) {
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,10f))


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun loadJSONFromAsset(): String? {
        val json = try {
            val inputStream  = assets.open("city.json")
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