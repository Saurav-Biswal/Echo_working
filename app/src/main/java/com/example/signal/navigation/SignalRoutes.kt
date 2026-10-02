package com.example.signal.navigation

object SignalRoutes {

    const val HOME = "home"

    const val SEARCH = "search"

    const val CAPTURE = "capture"

    const val LIBRARY = "library"

    const val DETAILS = "details"

    const val DETAILS_WITH_ID =
        "details/{memoryId}"

    fun details(memoryId: Long): String {
        return "details/$memoryId"
    }
}