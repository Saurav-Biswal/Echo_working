package com.example.signal.data.location

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class SignalGeofenceManager(
    private val context: Context
) {

    companion object {

        private const val TAG =
            "SignalGeofence"

        private const val GEOFENCE_RADIUS_METERS =
            200f
    }

    private val geofencingClient: GeofencingClient =
        LocationServices.getGeofencingClient(
            context
        )

    private val geofencePendingIntent: PendingIntent by lazy {

        val intent =
            Intent(
                context,
                SignalGeofenceReceiver::class.java
            )

        PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_MUTABLE
        )
    }

    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    fun addGeofence(
        memoryId: Long,
        latitude: Double,
        longitude: Double
    ) {

        val requestId =
            "memory_$memoryId"

        Log.d(
            TAG,
            "REGISTERING GEOFENCE: " +
                    "id=$requestId " +
                    "lat=$latitude " +
                    "lng=$longitude"
        )

        val geofence =
            Geofence.Builder()
                .setRequestId(
                    requestId
                )
                .setCircularRegion(
                    latitude,
                    longitude,
                    GEOFENCE_RADIUS_METERS
                )
                .setExpirationDuration(
                    Geofence.NEVER_EXPIRE
                )
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER
                )
                .build()

        val request =
            GeofencingRequest.Builder()
                .setInitialTrigger(
                    GeofencingRequest.INITIAL_TRIGGER_ENTER
                )
                .addGeofence(
                    geofence
                )
                .build()

        geofencingClient
            .addGeofences(
                request,
                geofencePendingIntent
            )
            .addOnSuccessListener {

                Log.d(
                    TAG,
                    "GEOFENCE REGISTERED SUCCESSFULLY: $requestId"
                )
            }
            .addOnFailureListener { exception ->

                val statusCode =
                    (
                            exception as?
                                    com.google.android.gms.common.api.ApiException
                            )
                        ?.statusCode

                val errorMessage =
                    if (statusCode != null) {

                        GeofenceStatusCodes
                            .getStatusCodeString(
                                statusCode
                            )

                    } else {

                        exception.message
                            ?: "Unknown error"
                    }

                Log.e(
                    TAG,
                    "GEOFENCE REGISTRATION FAILED: " +
                            "$requestId | $errorMessage",
                    exception
                )
            }
    }

    fun removeGeofence(
        memoryId: Long
    ) {

        val requestId =
            "memory_$memoryId"

        geofencingClient
            .removeGeofences(
                listOf(
                    requestId
                )
            )
            .addOnSuccessListener {

                Log.d(
                    TAG,
                    "GEOFENCE REMOVED: $requestId"
                )
            }
            .addOnFailureListener { exception ->

                Log.e(
                    TAG,
                    "FAILED TO REMOVE GEOFENCE: $requestId",
                    exception
                )
            }
    }
}