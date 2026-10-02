package com.example.signal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.signal.navigation.SignalNavGraph
import com.example.signal.ui.theme.SignalTheme

@Composable
fun SignalApp() {

    val navController =
        rememberNavController()

    var openedMemoryId by remember {
        mutableStateOf<Long?>(null)
    }

    SignalTheme {

        SignalNavGraph(
            navController = navController,
            sharedText = null,
            openedMemoryId = openedMemoryId
        )
    }
}