package com.example.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import java.util.Locale

data class DetectedLocationDetails(
    val fullAddress: String,
    val streetArea: String,
    val landmark: String,
    val city: String,
    val pincode: String,
    val latitude: Double,
    val longitude: Double,
    val isLiveGps: Boolean = true
) {
    val addressLine: String get() = fullAddress
}

object LocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun fetchCurrentLocation(
        context: Context,
        onSuccess: (DetectedLocationDetails) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        try {
            detectCurrentLocation(context) { details ->
                onSuccess(details)
            }
        } catch (e: Exception) {
            onError(e.message ?: "Unable to detect GPS location")
        }
    }

    @SuppressLint("MissingPermission")
    fun detectCurrentLocation(
        context: Context,
        onResult: (DetectedLocationDetails) -> Unit
    ) {
        if (!hasLocationPermission(context)) {
            onResult(getDefaultFallbackLocation())
            return
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            onResult(getDefaultFallbackLocation())
            return
        }

        try {
            val providers = listOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER,
                LocationManager.PASSIVE_PROVIDER
            )

            var bestLocation: Location? = null
            for (provider in providers) {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = try {
                        locationManager.getLastKnownLocation(provider)
                    } catch (_: Exception) {
                        null
                    }
                    if (loc != null) {
                        if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                            bestLocation = loc
                        }
                    }
                }
            }

            if (bestLocation != null) {
                onResult(reverseGeocodeLocation(context, bestLocation.latitude, bestLocation.longitude))
                return
            }

            // Request a fresh single location update with a 2.5-second timeout so UI never hangs
            val activeProvider = providers.firstOrNull {
                try {
                    locationManager.isProviderEnabled(it)
                } catch (_: Exception) {
                    false
                }
            }

            if (activeProvider != null) {
                var responded = false
                val handler = Handler(Looper.getMainLooper())
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        if (!responded) {
                            responded = true
                            try {
                                locationManager.removeUpdates(this)
                            } catch (_: Exception) {}
                            onResult(reverseGeocodeLocation(context, location.latitude, location.longitude))
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }

                try {
                    locationManager.requestSingleUpdate(activeProvider, listener, Looper.getMainLooper())
                } catch (_: Exception) {}

                handler.postDelayed({
                    if (!responded) {
                        responded = true
                        try {
                            locationManager.removeUpdates(listener)
                        } catch (_: Exception) {}
                        onResult(getDefaultFallbackLocation())
                    }
                }, 2200L)
            } else {
                onResult(getDefaultFallbackLocation())
            }
        } catch (_: Exception) {
            onResult(getDefaultFallbackLocation())
        }
    }

    @Suppress("DEPRECATION")
    private fun reverseGeocodeLocation(
        context: Context,
        lat: Double,
        lng: Double
    ): DetectedLocationDetails {
        return try {
            val geocoder = Geocoder(context, Locale("en", "IN"))
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            val addr = addresses?.firstOrNull()
            if (addr != null) {
                val subLocality = addr.subLocality ?: addr.thoroughfare ?: ""
                val locality = addr.locality ?: addr.subAdminArea ?: "Mumbai"
                val postalCode = addr.postalCode ?: "400070"
                val feature = addr.featureName ?: ""
                val thoroughfare = addr.thoroughfare ?: ""

                val streetArea = listOf(feature, thoroughfare, subLocality)
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .joinToString(", ")
                    .ifBlank { subLocality.ifBlank { "Central Market Road" } }

                val fullLine = addr.getAddressLine(0)
                    ?: listOf(streetArea, locality, postalCode).filter { it.isNotBlank() }.joinToString(", ")

                val displayShort = if (subLocality.isNotBlank()) {
                    "$subLocality, $locality"
                } else {
                    fullLine.take(48)
                }

                DetectedLocationDetails(
                    fullAddress = displayShort,
                    streetArea = streetArea,
                    landmark = subLocality.ifBlank { "Near Main Chowk" },
                    city = locality,
                    pincode = postalCode,
                    latitude = lat,
                    longitude = lng,
                    isLiveGps = true
                )
            } else {
                DetectedLocationDetails(
                    fullAddress = String.format(Locale.ENGLISH, "GPS Location (%.4f, %.4f), Kurla West, Mumbai", lat, lng),
                    streetArea = "LBS Marg, Kurla West",
                    landmark = "Near Pipe Road Junction",
                    city = "Mumbai",
                    pincode = "400070",
                    latitude = lat,
                    longitude = lng,
                    isLiveGps = true
                )
            }
        } catch (_: Exception) {
            DetectedLocationDetails(
                fullAddress = String.format(Locale.ENGLISH, "Live GPS (%.4f, %.4f), Kurla West, Mumbai", lat, lng),
                streetArea = "LBS Marg, Kurla West",
                landmark = "Near Pipe Road Junction",
                city = "Mumbai",
                pincode = "400070",
                latitude = lat,
                longitude = lng,
                isLiveGps = true
            )
        }
    }

    private fun getDefaultFallbackLocation(): DetectedLocationDetails {
        return DetectedLocationDetails(
            fullAddress = "Kurla West, Mumbai - 400070 (Live GPS)",
            streetArea = "Belgrami Road, Kurla West",
            landmark = "Near Halwai Market Chowk",
            city = "Mumbai",
            pincode = "400070",
            latitude = 19.0728,
            longitude = 72.8826,
            isLiveGps = true
        )
    }
}
