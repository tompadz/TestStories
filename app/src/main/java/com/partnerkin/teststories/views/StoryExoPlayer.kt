package com.partnerkin.teststories.views

import android.content.Context
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.upstream.DefaultAllocator

class StoryExoPlayer(private val context:Context) {

    private val MIN_BUFFER_DURATION = 2000 //Minimum Video you want to buffer while Playing
    private val MAX_BUFFER_DURATION = 5000 //Max Video you want to buffer during PlayBack
    private val MIN_PLAYBACK_START_BUFFER = 1500  //Min Video you want to buffer before start Playing it
    private val MIN_PLAYBACK_RESUME_BUFFER = 2000  //Min video You want to buffer when user resumes video

    private val loadController = DefaultLoadControl.Builder()
        .setAllocator(DefaultAllocator(true, 16))
        .setBufferDurationsMs(
            MIN_BUFFER_DURATION,
            MAX_BUFFER_DURATION,
            MIN_PLAYBACK_START_BUFFER,
            MIN_PLAYBACK_RESUME_BUFFER
        )
        .setTargetBufferBytes(-1)
        .setPrioritizeTimeOverSizeThresholds(true)
        .build()

    fun buildPlayer():ExoPlayer {
        return ExoPlayer.Builder(context)
            .setLoadControl(loadController)
            .build()
    }

}