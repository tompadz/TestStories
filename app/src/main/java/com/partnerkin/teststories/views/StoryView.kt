package com.partnerkin.teststories.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColor
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.partnerkin.teststories.views.progress_bar.StoriesProgressView
import com.partnerkin.teststories.models.StoryMedia
import com.partnerkin.teststories.utils.AndroidUtil.Companion.getDeviceWidth
import com.partnerkin.teststories.utils.AndroidUtil.Companion.pxFromDp
import com.partnerkin.teststories.utils.AndroidUtil.Companion.setCornerRadiusOfView
import com.partnerkin.teststories.views.listeners.ProgressListener
import com.partnerkin.teststories.views.listeners.StoryCompletionListener
import com.partnerkin.teststories.views.listeners.StoryLoadingListener

@Suppress("PrivatePropertyName")
class StoryView : LinearLayout {

    private val TAG = "StoryView"
    private val DEBUG = true
    private var LONG_CLICK_TIME_LIMIT = 500L
    private val VIEW_CORNER_RADIUS = 10f.pxFromDp(context).toFloat()
    private val CONTENT_BACKGROUND_COLOR = Color.parseColor("#373737").toColor()
    private val PROGRESS_HORIZONTAL_PADDING = 16f.pxFromDp(context)
    private val PROGRESS_TOP_PADDING = 16f.pxFromDp(context)

    private lateinit var contentLayout : FrameLayout
    private lateinit var buttonsLayout : LinearLayout
    private lateinit var storyPlayer : StoryPlayerView
    private lateinit var progressBar : StoriesProgressView

    private val screenWidth = resources.getDeviceWidth()
    private val storyMedias = mutableListOf<StoryMedia>()
    private var storyCompletionListener : StoryCompletionListener? = null
    private var currentMediaIndex = 0
    private var storyPressingTime = 0L

    var isPause = false
        private set

    constructor(context: Context) : super(context, null) {
        initAllViews()
    }

    constructor(context : Context, attributeSet : AttributeSet) : super(context, attributeSet) {
        initAllViews()
    }

    /**
     * Create and init views functions
     */

    private fun initAllViews() {
        initRootView()
        createContentLayout()
        createButtonsLayout()
        createStoryPlayer()
        createProgressBar()
    }

    private fun initRootView() {
        orientation = VERTICAL
        setBackgroundColor(Color.BLACK)
    }

    private fun createContentLayout() {
        contentLayout = FrameLayout(context).apply {
            layoutParams = LayoutParams(
                MATCH_PARENT,
                MATCH_PARENT,
                1f
            )
            background = CONTENT_BACKGROUND_COLOR.toDrawable()
            setCornerRadiusOfView(VIEW_CORNER_RADIUS)
            setOnTouchListener(onTouchListener)
        }
        addView(contentLayout)
    }

    private fun createButtonsLayout() {
        buttonsLayout = LinearLayout(context).apply {
            layoutParams = LayoutParams(
                MATCH_PARENT,
                200,
            )
        }
        addView(buttonsLayout)
    }

    private fun createStoryPlayer() {
        storyPlayer = StoryPlayerView(context).apply {
            layoutParams = LayoutParams(
                MATCH_PARENT,
                MATCH_PARENT
            )
            setLoadingListener(storyPlayerLoadingListener)
        }
        contentLayout.addView(storyPlayer)
    }

    private fun createProgressBar() {
        progressBar = StoriesProgressView(context).apply {
            layoutParams = LayoutParams(
                MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                gravity = Gravity.TOP
                setMargins(
                    PROGRESS_HORIZONTAL_PADDING,
                    PROGRESS_TOP_PADDING,
                    PROGRESS_HORIZONTAL_PADDING,
                    0
                )
            }
            setProgressListener(progressListener)
        }
        contentLayout.addView(progressBar)
    }

    /**
     * Public functions
     */

    fun setPreview(preview:String, lowPreview:String) = storyPlayer.setPreview(preview, lowPreview)

    fun showPreview() = storyPlayer.showPreview()

    fun showVideo()  = storyPlayer.showVideo()

    fun getPlayer() : ExoPlayer? = storyPlayer.player

    fun pause() {
        progressBar.pause()
        exoPlayer?.pause()
        debugLogI("onPause")
        isPause = true
    }

    fun resume() {
        progressBar.resume()
        exoPlayer?.play()
        debugLogI("onResume")
        isPause = false
    }

    fun setStoryCompletionListener(listener : StoryCompletionListener) {
        storyCompletionListener = listener
    }

    var exoPlayer : ExoPlayer? = null
        set(value) {
            field = value
            if (value == null) {
                progressBar.destroy()
            }
            debugLogI("exo player setup - $exoPlayer")
            storyPlayer.player = value
        }

    fun setStories(list : List<StoryMedia>) {
        debugLogI("onSetStories | stories size - ${list.size}")
        currentMediaIndex = 0
        progressBar.setStoriesCount(list.size)
        storyMedias.apply {
            clear()
            addAll(list)
        }
        setStoryPreview(0, false)
    }

    fun loadFirstVideo(player : ExoPlayer) {
        debugLogI("on load first video")
        currentMediaIndex = 0
        val storyMedia = storyMedias[0]
        setStoryPreview(0, false)
        progressBar.setStoriesCount(storyMedias.size)
        progressBar.destroy()
        player.stop(true)
        player.setMediaItem(MediaItem.fromUri(storyMedia.url))
        player.prepare()
    }

    /**
     * Private functions
     */

    private fun setStoryPreview(index:Int, show:Boolean) {
        val storyMedia = storyMedias[index]
        setPreview(storyMedia.preview, storyMedia.preview_low)
        if (show) showPreview()
    }

    private fun playStoryMedia() {
        val storyMedia = storyMedias[currentMediaIndex]
        setStoryPreview(currentMediaIndex, true)
        debugLogI("load story media from index - $currentMediaIndex")
        exoPlayer !!.stop(true)
        exoPlayer !!.setMediaItem(MediaItem.fromUri(storyMedia.url))
        exoPlayer !!.prepare()
    }

    private fun setProgressDuration() {
        try {
            val duration = storyPlayer.duration !!
            progressBar.setCurrentStoryDuration(duration, currentMediaIndex)
            progressBar.startStories(currentMediaIndex)
        }catch (t:Throwable) {
            progressBar.setStoriesCount(storyMedias.size)
            progressBar.destroy()
            debugLogE(t.message ?: "error")
        }
    }

    private fun checkStoryClick(newTime : Long, event : MotionEvent) : Boolean {
        val clickTime = newTime - storyPressingTime
        return when {
            clickTime > LONG_CLICK_TIME_LIMIT -> {
                resume()
                true
            }
            else -> {
                val xPos = event.rawX
                val halfScreen = screenWidth / 2
                if (xPos > halfScreen) {
                    progressBar.skip()
                } else {
                    progressBar.reverse()
                }
                resume()
                true
            }
        }
    }

    private fun debugLogI(text:String) {
        if (DEBUG) Log.i(TAG, text)
    }
    private fun debugLogE(text : String) {
        if (DEBUG) Log.e(TAG, text)
    }

    /**
     * Listeners
     */

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = OnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                storyPressingTime = System.currentTimeMillis()
                pause()
                return@OnTouchListener true
            }
            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                return@OnTouchListener checkStoryClick(now, event)
            }
        }
        false
    }

    private val storyPlayerLoadingListener = object : StoryLoadingListener {
        override fun onStoryLoading(isLoading : Boolean) {
            if (! isLoading) {
                resume()
                setProgressDuration()
            }
        }
    }

    private val progressListener = object : ProgressListener {
        override fun onNext() {
            debugLogI("on story skip")
            if (currentMediaIndex != storyMedias.size - 1) {
                ++currentMediaIndex
                playStoryMedia()
            }
        }

        override fun onPrev() {
            debugLogI("on story reverse")
            if (currentMediaIndex != 0) {
                --currentMediaIndex
            }
            playStoryMedia()
        }

        override fun onComplete() {
            debugLogI("on story complete")
            exoPlayer?.stop(true)
            setStoryPreview(0, true)
            storyCompletionListener?.onComplete()
        }
    }

    /**
     * override functions
     */
}