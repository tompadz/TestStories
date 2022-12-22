package com.partnerkin.teststories.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColor
import androidx.media3.exoplayer.ExoPlayer
import com.partnerkin.teststories.utils.AndroidUtil.Companion.pxFromDp
import com.partnerkin.teststories.utils.AndroidUtil.Companion.setCornerRadiusOfView

class StoryView : LinearLayout {

    private val TAG = "StoryView"
    private val VIEW_CORNER_RADIUS = 10f.pxFromDp(context).toFloat()
    private val CONTENT_BACKGROUND_COLOR = Color.parseColor("#373737").toColor()

    private lateinit var contentLayout : FrameLayout
    private lateinit var buttonsLayout : LinearLayout
    private lateinit var storyPlayer : StoryPlayerView

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
        }
        contentLayout.addView(storyPlayer)
    }

    /**
     * Public functions
     */

    fun setPreview(url:String) = storyPlayer.setPreview(url)
    fun showPreview() = storyPlayer.showPreview()
    fun showVideo()  = storyPlayer.showVideo()

    fun setPlayer(player : ExoPlayer?) {
        storyPlayer.player = player
    }

    fun getPlayer() : ExoPlayer? = storyPlayer.player

    /**
     * Private functions
     */



    /**
     * override functions
     */
}