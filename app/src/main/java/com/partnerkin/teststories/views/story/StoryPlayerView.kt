package com.partnerkin.teststories.views.story

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.partnerkin.teststories.utils.AndroidUtil.Companion.pxFromDp
import com.partnerkin.teststories.utils.MaterialMotion
import com.partnerkin.teststories.views.listeners.StoryLoadingListener
import jp.wasabeef.glide.transformations.BlurTransformation

@Suppress("PrivatePropertyName")
class StoryPlayerView : FrameLayout {

    private val TAG = "StoryPlayerView"
    private val PROGRESS_CARD_COLOR = Color.parseColor("#41000000")
    private val PROGRESS_CARD_PADDING = 2f.pxFromDp(context)
    private val PROGRESS_CARD_RADIUS = 50f.pxFromDp(context).toFloat()
    private val PROGRESS_BAR_SIZE = 28f.pxFromDp(context)
    private val PLAYER_RESIZE_MODE = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

    private lateinit var playerView : PlayerView
    private lateinit var previewView : ImageView

    private lateinit var progressBar : ProgressBar
    private lateinit var progressCardContainer : CardView

    private var listener : StoryLoadingListener? = null

    var isLoading = true
        private set(value) {
            field = value
            listener?.onStoryLoading(value)
        }

    var isError = false
        private set

    constructor(context: Context) : super(context, null) {
        initAllViews()
    }

    constructor(context : Context, attributeSet : AttributeSet) : super(context, attributeSet) {
        initAllViews()
    }

    private fun initAllViews() {
        createImagePreview()
        createVideoPlayer()
        createProgressBar()
        createProgressCard()
    }

    private fun createImagePreview() {
        previewView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        addView(previewView)
    }

    private fun createVideoPlayer() {
        playerView = PlayerView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            useController = false
            resizeMode = PLAYER_RESIZE_MODE
            setKeepContentOnPlayerReset(true)
        }
        addView(playerView)
    }

    private fun createProgressCard() {
        progressCardContainer = CardView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
            setContentPadding(
                PROGRESS_CARD_PADDING,
                PROGRESS_CARD_PADDING,
                PROGRESS_CARD_PADDING,
                PROGRESS_CARD_PADDING
            )
            setCardBackgroundColor(
                PROGRESS_CARD_COLOR
            )
            cardElevation = 0f
            radius = PROGRESS_CARD_RADIUS
            addView(progressBar)
        }
        addView(progressCardContainer)
    }

    private fun createProgressBar() {
        progressBar = CircularProgressIndicator(context).apply {
            layoutParams = LayoutParams(
                PROGRESS_BAR_SIZE,
                PROGRESS_BAR_SIZE,
            )
            indicatorSize = PROGRESS_BAR_SIZE
            indicatorInset = 4f.pxFromDp(context)
            trackThickness = 3f.pxFromDp(context)
            setIndicatorColor(Color.WHITE)
            isIndeterminate = true
        }
    }

    /**
     * Public functions
     */

    fun setLoadingListener(listener: StoryLoadingListener) {
        this.listener = listener
    }

    fun setPreview(preview:String, lowPreview:String) {
        Glide.with(context)
            .load(preview)
            .thumbnail(
                Glide.with(context)
                    .load(lowPreview)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(55, 3)))
            )
            .transition(DrawableTransitionOptions.withCrossFade(100))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(previewView)
    }

    fun showPreview() {
        playerView.visibility = View.GONE
        previewView.visibility = View.VISIBLE
    }

    fun showVideo() {
        previewView.visibility = View.INVISIBLE
        playerView.visibility = View.VISIBLE
    }

    var player : ExoPlayer? = null
        set(value) {
            playerView.player = value
            field = value
            if (value != null) {
                addPlayerLoadingListener()
            }
        }

    val duration get() = player?.duration

    /**
     * Private functions
     */

    private fun showLoading(state:Boolean) {
        MaterialMotion().addAxisAnimation(this)
        progressCardContainer.isVisible = state
        isLoading = state
    }

    private fun addPlayerLoadingListener() {
        player !!.addListener(playerLoadingListener)
    }

    private fun removePlayerLoadingListener() {
        player !!.addListener(playerLoadingListener)
    }

    private val playerLoadingListener = object : Player.Listener {
        @SuppressLint("SwitchIntDef")
        override fun onPlaybackStateChanged(playbackState : Int) {
            when(playbackState) {
                Player.STATE_BUFFERING -> {
                    if (!isLoading) {
                        showLoading(true)
                    }
                }
                Player.STATE_READY -> {
                    if (isLoading) {
                        showLoading(false)
                    }
                }
            }
        }

        //TODO
        override fun onPlayerError(error : PlaybackException) {
            isError = true
            showLoading(false)
            Snackbar.make(this@StoryPlayerView, error.message ?: "error", Snackbar.LENGTH_LONG).show()
        }
    }
}