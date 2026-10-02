package com.example.signal.data.location

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.signal.MainActivity
import com.example.signal.R

object SignalNotificationHelper {

    private const val CHANNEL_ID = "signal_location"
    private const val CHANNEL_NAME = "Signal Location"
    private const val CHANNEL_DESCRIPTION =
        "Location-based Signal memory reminders"

    const val EXTRA_MEMORY_ID =
        "signal_memory_id"

    private fun createChannel(
        context: Context
    ) {

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                description =
                    CHANNEL_DESCRIPTION
            }

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.createNotificationChannel(
            channel
        )
    }

    @RequiresPermission(
        Manifest.permission.POST_NOTIFICATIONS
    )
    fun showMemoryLocationNotification(
        context: Context,
        memoryId: Long,
        title: String,
        summary: String
    ) {

        createChannel(
            context
        )

        val intent =
            Intent(
                context,
                MainActivity::class.java
            ).apply {

                putExtra(
                    EXTRA_MEMORY_ID,
                    memoryId
                )

                flags =
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                memoryId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notificationText =
            if (
                summary.isNotBlank()
            ) {

                summary

            } else {

                "You saved this memory for this location."
            }

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle(
                    title
                )
                .setContentText(
                    notificationText
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            notificationText
                        )
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setContentIntent(
                    pendingIntent
                )
                .setAutoCancel(
                    true
                )
                .build()

        NotificationManagerCompat
            .from(
                context
            )
            .notify(
                memoryId.toInt(),
                notification
            )
    }
}