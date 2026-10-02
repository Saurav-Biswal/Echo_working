package com.example.signal.data.source

import com.example.signal.data.ai.SignalAiService

class ContentAdapterFactory(
    private val aiService: SignalAiService
) {

    fun getAdapter(
        url: String
    ): ContentAdapter {

        return when (
            SourceDetector.detect(
                url
            )
        ) {

            SourceType.WEB -> {

                WebContentAdapter(
                    aiService
                )
            }

            SourceType.INSTAGRAM -> {

                InstagramContentAdapter(
                    aiService
                )
            }

            SourceType.YOUTUBE -> {

                YouTubeContentAdapter(
                    aiService
                )
            }

            SourceType.FACEBOOK -> {

                SocialContentAdapter(
                    aiService,
                    sourceName = "Facebook post or shared content"
                )
            }

            SourceType.X -> {

                SocialContentAdapter(
                    aiService,
                    sourceName = "X/Twitter post or shared content"
                )
            }
        }
    }
}