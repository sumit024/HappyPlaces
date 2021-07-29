package com.app_devs.happyplaces.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app_devs.happyplaces.R
import com.app_devs.happyplaces.models.HappyPlaceModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mHappyPlaceDetail:HappyPlaceModel?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS))
        {
            mHappyPlaceDetail=intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)
        }
        if(mHappyPlaceDetail!=null)
        {
            setSupportActionBar(toolbarMap)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title =mHappyPlaceDetail!!.title

            toolbarMap.setNavigationOnClickListener {
                onBackPressed()
            }

            val supportMapFragment:SupportMapFragment=
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            supportMapFragment.getMapAsync(this)
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        val position=LatLng(mHappyPlaceDetail!!.latitude,mHappyPlaceDetail!!.longitude)
        googleMap.addMarker(MarkerOptions().position(position).title(mHappyPlaceDetail!!.location))
        val newLatLongZoom=CameraUpdateFactory.newLatLngZoom(position,10f)
        googleMap.animateCamera(newLatLongZoom)

    }
}