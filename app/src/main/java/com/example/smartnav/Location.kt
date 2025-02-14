package com.example.smartnav

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Location(private val activity: Activity, private val map: GoogleMap) {

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val DIRECTIONS_API_KEY = "AIzaSyAXPdsbCHSjG2VcpBmPTQDVpQ5LFZNVHQo"
    }

    fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun getDeviceLocation(callback: (LatLng) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    callback(currentLatLng)
                }
            }
        }
    }

    // Methode, um mit Berechtigungsanfragen umzugehen
    fun handlePermissionResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation()
            }
        }
    }

    fun drawRouteToDestination(currentLocation: LatLng, destination: LatLng) {
        val url = getDirectionsUrl(currentLocation, destination)

        // Coroutine im IO-Thread für die Netzwerkanfrage starten
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                if (!responseData.isNullOrEmpty()) {
                    val jsonResponse = JSONObject(responseData)
                    val routes = jsonResponse.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val points = routes.getJSONObject(0)
                            .getJSONObject("overview_polyline")
                            .getString("points")

                        val decodedPath = PolyUtil.decode(points)
                        // Zurück zum Hauptthread, um die Karte zu aktualisieren
                        withContext(Dispatchers.Main) {
                            map.addPolyline(
                                PolylineOptions().addAll(decodedPath).color(Color.GREEN)
                                    .width(10f)
                            )
                            map.addMarker(
                                MarkerOptions().position(destination).title("Parkplatz")
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getDirectionsUrl(origin: LatLng, destination: LatLng): String {
        return "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&key=$DIRECTIONS_API_KEY"
    }

    fun getNearbyParking(currentLocation: LatLng, callback: (LatLng?) -> Unit) {
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=${currentLocation.latitude},${currentLocation.longitude}" +
                "&radius=500" + // Suchradius 500m
                "&type=parking" + // Suche nach Parkplatz
                "&key=$DIRECTIONS_API_KEY"

        CoroutineScope(Dispatchers.IO).launch {

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                if (!responseData.isNullOrEmpty()) {
                    val jsonResponse = JSONObject(responseData)
                    val results = jsonResponse.getJSONArray("results")
                    if (results.length() > 0) {
                        val firstResult = results.getJSONObject(0)
                        val location =
                            firstResult.getJSONObject("geometry").getJSONObject("location")
                        val lat = location.getDouble("lat")
                        val lng = location.getDouble("lng")
                        callback(
                            LatLng(
                                lat,
                                lng
                            )
                        ) // Rückgabe der Koordinaten des ersten Ergebnisses

                    }
                }
            }
        }
    }
}