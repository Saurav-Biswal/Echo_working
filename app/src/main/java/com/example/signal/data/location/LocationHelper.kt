package com.example.signal.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(
    private val context: Context
) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {

        val fine =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarse =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationResult? {

        if (!hasLocationPermission()) {
            return null
        }

        return suspendCancellableCoroutine { continuation ->

            fusedLocationClient
                .getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                )
                .addOnSuccessListener { location ->

                    if (location != null) {

                        continuation.resume(
                            LocationResult(
                                latitude = location.latitude,
                                longitude = location.longitude
                            )
                        )

                    } else {

                        fusedLocationClient
                            .lastLocation
                            .addOnSuccessListener { lastLocation ->

                                if (lastLocation != null) {

                                    continuation.resume(
                                        LocationResult(
                                            latitude = lastLocation.latitude,
                                            longitude = lastLocation.longitude
                                        )
                                    )

                                } else {

                                    continuation.resume(null)
                                }
                            }
                            .addOnFailureListener {
                                continuation.resume(null)
                            }
                    }
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
    }
}

data class LocationResult(
    val latitude: Double,
    val longitude: Double
) {

    fun asString(): String {
        return "$latitude,$longitude"
    }
}