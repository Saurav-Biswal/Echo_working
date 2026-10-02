package com.example.signal.data.source

object SourceDetector {

    fun detect(url: String): SourceType {

        val value = url.lowercase()

        return when {

            value.contains("instagram.com") ||
                    value.contains("instagr.am") -> {
                SourceType.INSTAGRAM
            }

            value.contains("youtube.com") ||
                    value.contains("youtu.be") -> {
                SourceType.YOUTUBE
            }

            value.contains("facebook.com") ||
                    value.contains("fb.watch") -> {
                SourceType.FACEBOOK
            }

            value.contains("x.com") ||
                    value.contains("twitter.com") -> {
                SourceType.X
            }

            else -> {
                SourceType.WEB
            }
        }
    }
}