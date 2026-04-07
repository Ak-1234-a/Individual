package com.example.freelancer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var ivProfilePhoto: ImageView

    private lateinit var switchWifi: Switch
    private lateinit var switchBluetooth: Switch

    private lateinit var wifiManager: WifiManager
    private var bluetoothAdapter: BluetoothAdapter? = null

    // Activity Result Launcher for Camera
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            ivProfilePhoto.setImageBitmap(imageBitmap)
        }
    }

    // Permission Launcher for Camera
    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) openCamera()
        else Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
    }

    // Permission Launcher for Bluetooth (Android 12+)
    private val requestBluetoothPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.BLUETOOTH_CONNECT] == true) {
            toggleBluetooth(true)
        } else {
            Toast.makeText(this, "Bluetooth Permission Denied", Toast.LENGTH_SHORT).show()
            switchBluetooth.isChecked = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Views
        tvLocationName = findViewById(R.id.tvLocationName)
        tvLatitude = findViewById(R.id.tvLatitude)
        tvLongitude = findViewById(R.id.tvLongitude)
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto)
        switchWifi = findViewById(R.id.switchWifi)
        switchBluetooth = findViewById(R.id.switchBluetooth)

        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)
        val btnTakePhoto = findViewById<Button>(R.id.btnTakePhoto)

        // System Managers
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // Initial States
        switchWifi.isChecked = wifiManager.isWifiEnabled
        switchBluetooth.isChecked = bluetoothAdapter?.isEnabled ?: false

        // Wi-Fi Control
        switchWifi.setOnCheckedChangeListener { _, isChecked ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10+, apps cannot toggle Wi-Fi directly. We open settings.
                val intent = Intent(Settings.Panel.ACTION_WIFI)
                startActivity(intent)
                Toast.makeText(this, "Please toggle Wi-Fi in the panel", Toast.LENGTH_SHORT).show()
            } else {
                @Suppress("DEPRECATION")
                wifiManager.isWifiEnabled = isChecked
            }
        }

        // Bluetooth Control
        switchBluetooth.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBluetoothPermissionAndEnable()
            } else {
                toggleBluetooth(false)
            }
        }

        btnTakePhoto.setOnClickListener {
            checkCameraPermission()
        }

        checkLocationPermission()

        btnEditProfile.setOnClickListener {
            val popup = PopupMenu(this, btnEditProfile)
            popup.menuInflater.inflate(R.menu.edit_profile_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.change_name -> { Toast.makeText(this, "Change Name Clicked", Toast.LENGTH_SHORT).show(); true }
                    R.id.change_email -> { Toast.makeText(this, "Change Email Clicked", Toast.LENGTH_SHORT).show(); true }
                    R.id.change_password -> { Toast.makeText(this, "Change Password Clicked", Toast.LENGTH_SHORT).show(); true }
                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun checkBluetoothPermissionAndEnable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestBluetoothPermissionLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH_CONNECT))
            } else {
                toggleBluetooth(true)
            }
        } else {
            toggleBluetooth(true)
        }
    }

    private fun toggleBluetooth(enable: Boolean) {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show()
            return
        }

        if (enable) {
            if (!bluetoothAdapter!!.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivity(enableBtIntent)
            }
        } else {
            if (bluetoothAdapter!!.isEnabled) {
                @Suppress("DEPRECATION")
                bluetoothAdapter!!.disable()
                Toast.makeText(this, "Bluetooth Disabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            takePictureLauncher.launch(takePictureIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        } else {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5f, this)
        }
    }

    override fun onLocationChanged(location: Location) {
        tvLatitude.text = "Latitude: ${location.latitude}"
        tvLongitude.text = "Longitude: ${location.longitude}"
        Thread {
            try {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val locationName = addresses?.getOrNull(0)?.getAddressLine(0) ?: "Unknown Location"
                runOnUiThread { tvLocationName.text = "Location: $locationName" }
            } catch (e: Exception) {
                runOnUiThread { tvLocationName.text = "Location: Error" }
            }
        }.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
    override fun onProviderEnabled(p0: String) {}
    override fun onProviderDisabled(p0: String) {}
}