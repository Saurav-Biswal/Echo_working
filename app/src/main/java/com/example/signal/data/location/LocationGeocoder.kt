package com.example.signal.data.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class LocationGeocoder(
    private val context: Context
) {

    suspend fun findCoordinates(
        placeName: String
    ): GeocodedLocation? {

        if (placeName.isBlank()) {
            return null
        }

        return withContext(Dispatchers.IO) {

            try {

                val geocoder =
                    Geocoder(
                        context,
                        Locale.getDefault()
                    )

                @Suppress("DEPRECATION")
                val addresses =
                    geocoder.getFromLocationName(
                        placeName,
                        1
                    )

                val address =
                    addresses?.firstOrNull()
                        ?: return@withContext null

                GeocodedLocation(
                    name = placeName,
                    latitude = address.latitude,
                    longitude = address.longitude
                )

            } catch (e: Exception) {

                null
            }
        }
    }
}


data class GeocodedLocation(

    val name: String,

    val latitude: Double,

    val longitude: Double
)