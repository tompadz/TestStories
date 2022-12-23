package com.partnerkin.teststories.views.progress_bar

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import com.partnerkin.teststories.R


class StoryProgressBar(context:Context) : FrameLayout(context, null) {

    private val DEFAULT_PROGRESS_DURATION = 2000
    private val PROGRESS_SECONDARY = Color.parseColor("#8affffff")

    private var frontProgressView : View
    private var maxProgressView : View

    private var animation : StoryProgressScaleAnimation? = null
    private var duration = DEFAULT_PROGRESS_DURATION.toLong()
    private var callback : Callback? = null

    interface Callback {
        fun onStartProgress()
        fun onFinishProgress()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_story_progress, this);
        frontProgressView = findViewById(R.id.front_progress)
        maxProgressView = findViewById(R.id.max_progress)
    }

    fun setDuration(duration : Long) {
        this.duration = duration
    }

    fun setCallback(callback : Callback) {
        this.callback = callback
    }

    fun setMax() {
        finishProgress(true)
    }

    fun setMin() {
        finishProgress(false)
    }

    fun setMinWithoutCallback() {
        maxProgressView.setBackgroundColor(PROGRESS_SECONDARY)
        maxProgressView.visibility = VISIBLE
        if (animation != null) {
            animation !!.setAnimationListener(null)
            animation !!.cancel()
        }
    }

    fun setMaxWithoutCallback() {
        maxProgressView.setBackgroundColor(Color.WHITE)
        maxProgressView.visibility = VISIBLE
        if (animation != null) {
            animation !!.setAnimationListener(null)
            animation !!.cancel()
        }
    }

    fun startProgress() {
        maxProgressView.visibility = GONE
        animation = StoryProgressScaleAnimation(0f, 1f, 1f, 1f, Animation.ABSOLUTE, 0f, Animation.RELATIVE_TO_SELF, 0f)
        animation !!.duration = duration
        animation !!.interpolator = LinearInterpolator()
        animation !!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation : Animation) {
                frontProgressView.visibility = VISIBLE
                if (callback != null) callback !!.onStartProgress()
            }
            override fun onAnimationEnd(animation : Animation) {
                if (callback != null) callback !!.onFinishProgress()
            }
            override fun onAnimationRepeat(animation : Animation) {}
        })
        animation !!.fillAfter = true
        frontProgressView.startAnimation(animation)
    }

    fun pauseProgress() {
        if (animation != null) {
            animation !!.pause()
        }
    }

    fun resumeProgress() {
        if (animation != null) {
            animation !!.resume()
        }
    }

    fun clear() {
        if (animation != null) {
            animation !!.setAnimationListener(null)
            animation !!.cancel()
            animation = null
        }
    }

    private fun finishProgress(isMax : Boolean) {
        if (isMax) maxProgressView.setBackgroundColor(Color.WHITE)
        maxProgressView.visibility = if (isMax) VISIBLE else GONE
        if (animation != null) {
            animation !!.setAnimationListener(null)
            animation !!.cancel()
            if (callback != null) {
                callback !!.onFinishProgress()
            }
        }
    }

}