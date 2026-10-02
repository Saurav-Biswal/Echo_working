package com.example.signal

import androidx.compose.runtime.mutableStateOf
import com.example.signal.data.location.SignalNotificationHelper
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.signal.navigation.SignalNavGraph
import com.example.signal.navigation.SignalRoutes
import com.example.signal.ui.theme.SignalTheme

class MainActivity : ComponentActivity() {

    private var sharedText: String? = null

    private var openedMemoryId =
        mutableStateOf<Long?>(null)

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            // Permission result received.
        }

    private val foregroundLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            requestBackgroundLocationPermission()
        }

    private val backgroundLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            // Background location permission result received.
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        sharedText =
            extractSharedText(intent)

        openedMemoryId.value =
            intent.getLongExtra(
                SignalNotificationHelper.EXTRA_MEMORY_ID,
                -1L
            ).takeIf { it != -1L }

        requestNotificationPermission()
        requestLocationPermissions()

        setContent {

            SignalTheme {

                val navController =
                    rememberNavController()

                SignalNavGraph(
                    navController = navController,
                    sharedText = sharedText,
                    openedMemoryId = openedMemoryId.value
                )

                LaunchedEffect(sharedText) {

                    if (!sharedText.isNullOrBlank()) {

                        navController.navigate(
                            SignalRoutes.CAPTURE
                        ) {
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(
        intent: Intent
    ) {
        super.onNewIntent(intent)

        setIntent(intent)

        sharedText =
            extractSharedText(intent)

        openedMemoryId.value =
            intent.getLongExtra(
                SignalNotificationHelper.EXTRA_MEMORY_ID,
                -1L
            ).takeIf { it != -1L }
    }

    private fun requestNotificationPermission() {

        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.TIRAMISU
        ) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun requestLocationPermissions() {

        val fineGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            requestBackgroundLocationPermission()
            return
        }

        foregroundLocationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun requestBackgroundLocationPermission() {

        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.Q
        ) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                backgroundLocationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }
    }

    private fun extractSharedText(
        intent: Intent?
    ): String? {

        if (
            intent?.action !=
            Intent.ACTION_SEND
        ) {
            return null
        }

        return intent.getStringExtra(
            Intent.EXTRA_TEXT
        )
    }
}