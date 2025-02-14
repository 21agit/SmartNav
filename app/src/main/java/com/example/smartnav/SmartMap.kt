package com.example.smartnav

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.smartnav.databinding.ActivitySmartMapBinding
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class SmartMap : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivitySmartMapBinding
    private val countdown = Countdown()
    private lateinit var locationHelper: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySmartMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val countdownText: TextView = findViewById(R.id.countdown_text)
        val startButton: Button = findViewById(R.id.start_button)

        startButton.setOnClickListener {
            if (!countdown.isCountingDown) {
                countdown.startCountdown(countdownText) {
                    // Nach Ablauf des Countdowns die aktuelle Position abrufen
                    locationHelper.getDeviceLocation { currentLocation ->
                        // Nahegelegene Parkplätze suchen
                        locationHelper.getNearbyParking(currentLocation) { parkingLocation ->
                            if (parkingLocation != null) {
                                // Route zum nächsten Parkplatz zeichnen
                                locationHelper.drawRouteToDestination(currentLocation, parkingLocation)
                            } else {
                                runOnUiThread {
                                    Toast.makeText(
                                        this,
                                        "Keine Parkplätze in der Nähe gefunden",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        locationHelper = Location(this, mMap)
        locationHelper.enableUserLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationHelper.handlePermissionResult(requestCode, grantResults)
    }
}