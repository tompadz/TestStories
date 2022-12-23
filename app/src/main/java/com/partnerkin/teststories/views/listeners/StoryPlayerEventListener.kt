package com.partnerkin.teststories.views.listeners

import androidx.media3.exoplayer.ExoPlayer


interface StoryPlayerEventListener {
    fun onPrePlay(player: ExoPlayer)
    fun onPlayCanceled()
    fun onPlay()
    fun onPause()
    fun onResume()
}