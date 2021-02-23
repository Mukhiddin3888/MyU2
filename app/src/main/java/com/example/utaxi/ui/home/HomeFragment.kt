package com.example.utaxi.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.utaxi.Common
import com.example.utaxi.R
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*


class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mMap: GoogleMap


    //online sysytem
    private lateinit var onlineRef : DatabaseReference
    private lateinit var currentUserRef: DatabaseReference
    private lateinit var driversLocationRef: DatabaseReference
    private lateinit var geoFire:GeoFire


    override fun onDestroy() {
        geoFire.removeLocation("firebase-hq");
        super.onDestroy()
    }


    override fun onResume() {
        super.onResume()

    }




    private val REQUEST_LOCATION_PERMISSION = 1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        init()

        return root
    }

    private fun init() {


        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected")

        driversLocationRef = FirebaseDatabase.getInstance().getReference(Common.DRIVERS_LOCATION_REFERENCE)

        geoFire = GeoFire(driversLocationRef)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Request permission

        val latitude = 41.338974833027486
        val longtitude = 69.33511885918323

        val homeLatLng = LatLng(latitude, longtitude)
        val zoomLevel = 15f

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
        mMap.addMarker(MarkerOptions().position(homeLatLng))


        enableMyLocation()
        setMapLongClick(mMap)
        setPoiClick(mMap)

        geoFire.setLocation(
            "firebase-hq",
            GeoLocation(37.7853889, -122.4056973),
            object : GeoFire.CompletionListener {
                fun onComplete(key: String?, error: FirebaseError?) {
                    if (error != null) {
                        System.err.println("There was an error saving the location to GeoFire: $error")
                    } else {
                        println("Location saved on server successfully!")
                    }
                }

                override fun onComplete(key: String?, error: DatabaseError?) {


                }
            })

    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(
                        this.requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                != PackageManager.PERMISSION_GRANTED) {

                return
            }
            mMap.isMyLocationEnabled = true

        } else {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    private fun setMapLongClick(map: GoogleMap){

        map.setOnMapLongClickListener {

            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1\$.5f, Long: %2\$.5f",
                it.latitude,
                it.longitude

            )

            map.addMarker(
                MarkerOptions().position(it).title(getString(R.string.app_name)).snippet(snippet)
            )
        }
    }
    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker.showInfoWindow()
        }
    }




}