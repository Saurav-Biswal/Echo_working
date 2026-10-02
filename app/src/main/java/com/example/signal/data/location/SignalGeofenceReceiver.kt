package com.example.signal.data.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.signal.data.local.SignalDatabase
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignalGeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val pendingResult =
            goAsync()

        val event =
            GeofencingEvent.fromIntent(
                intent
            )

        if (
            event == null
        ) {

            pendingResult.finish()
            return
        }

        if (
            event.hasError()
        ) {

            pendingResult.finish()
            return
        }

        if (
            event.geofenceTransition !=
            Geofence.GEOFENCE_TRANSITION_ENTER
        ) {

            pendingResult.finish()
            return
        }

        val triggeredGeofences =
            event.triggeringGeofences

        if (
            triggeredGeofences == null
        ) {

            pendingResult.finish()
            return
        }

        CoroutineScope(
            Dispatchers.IO
        ).launch {

            try {

                val database =
                    SignalDatabase.getDatabase(
                        context
                    )

                val memoryDao =
                    database.memoryDao()

                for (
                geofence in triggeredGeofences
                ) {

                    val memoryId =
                        geofence.requestId
                            .removePrefix(
                                "memory_"
                            )
                            .toLongOrNull()
                            ?: continue

                    val memory =
                        memoryDao.getMemoryById(
                            memoryId
                        )
                            ?: continue

                    SignalNotificationHelper
                        .showMemoryLocationNotification(
                            context = context,
                            memoryId = memory.id,
                            title = memory.title,
                            summary = memory.summary
                        )
                }

            } finally {

                pendingResult.finish()
            }
        }
    }
}