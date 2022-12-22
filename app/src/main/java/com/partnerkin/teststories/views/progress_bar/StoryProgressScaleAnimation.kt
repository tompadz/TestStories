package com.partnerkin.teststories.views.progress_bar

import android.view.animation.ScaleAnimation
import android.view.animation.Transformation

class StoryProgressScaleAnimation(
    fromX : Float,
    toX : Float,
    fromY : Float,
    toY : Float,
    pivotXType : Int,
    pivotXValue : Float,
    pivotYType : Int,
    pivotYValue : Float
) : ScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType,
    pivotYValue) {

    private var mElapsedAtPause : Long = 0
    private var mPaused = false

    override fun getTransformation(
        currentTime : Long,
        outTransformation : Transformation?,
        scale : Float,
    ) : Boolean {
        if (mPaused && mElapsedAtPause == 0L) {
            mElapsedAtPause = currentTime - startTime
        }
        if (mPaused) {
            startTime = currentTime - mElapsedAtPause
        }
        return super.getTransformation(currentTime, outTransformation, scale)
    }

    fun pause() {
        if (mPaused) return
        mElapsedAtPause = 0
        mPaused = true
    }

    fun resume() {
        mPaused = false
    }
}