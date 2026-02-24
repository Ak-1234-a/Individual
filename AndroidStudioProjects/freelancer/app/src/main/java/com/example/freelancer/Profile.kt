package com.example.freelancer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class Profile : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private val LOCATION_PERMISSION_REQUEST = 100

    private lateinit var tvLocationName: TextView
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvLocationName = findViewById(R.id.tvLocationName)
        tvLatitude = findViewById(R.id.tvLatitude)
        tvLongitude = findViewById(R.id.tvLongitude)

        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)

        // Location Manager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Request location permission
        checkLocationPermission()

        // Popup Menu
        btnEditProfile.setOnClickListener {
            val popup = PopupMenu(this, btnEditProfile)
            popup.menuInflater.inflate(R.menu.edit_profile_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.change_name -> {
                        Toast.makeText(this, "Change Name Clicked", Toast.LENGTH_SHORT).show(); true
                    }
                    R.id.change_email -> {
                        Toast.makeText(this, "Change Email Clicked", Toast.LENGTH_SHORT).show(); true
                    }
                    R.id.change_password -> {
                        Toast.makeText(this, "Change Password Clicked", Toast.LENGTH_SHORT).show(); true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                5f,
                this
            )
        }
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude

        tvLatitude.text = "Latitude: $latitude"
        tvLongitude.text = "Longitude: $longitude"

        // Geocoder in background
        Thread {
            try {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val locationName = if (addresses != null && addresses.isNotEmpty()) {
                    addresses[0].getAddressLine(0)
                } else {
                    "Unknown Location"
                }

                runOnUiThread {
                    tvLocationName.text = "Location: $locationName"
                }

            } catch (e: Exception) {
                runOnUiThread {
                    tvLocationName.text = "Location: Error"
                }
            }
        }.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
}