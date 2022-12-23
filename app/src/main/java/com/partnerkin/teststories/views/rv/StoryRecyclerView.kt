package com.partnerkin.teststories.views.rv

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.partnerkin.teststories.views.listeners.StoryPlayerEventListener
import com.partnerkin.teststories.views.story.StoryExoPlayer
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class StoryRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val playerVideo = StoryExoPlayer(context).buildPlayer()
    var currentVideoHolder: StoryPlayerEventListener? = null
    private var videoItemHeight = 0
    private var screenHeight = 0

    init {

        with(playerVideo) {
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_OFF
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) currentVideoHolder?.onPlay()
                }
            })
        }

        videoViewHolderChanges()
            .onEach { playVideo(it) }
            .launchIn((context as LifecycleOwner).lifecycleScope)

        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {}

            override fun onChildViewDetachedFromWindow(view: View) {
                val holder = findContainingViewHolder(view)
                if (holder is StoryPlayerEventListener) {
                    holder.onPlayCanceled()
                }
            }
        })
        val screenSize = screenSize()
        videoItemHeight = screenSize.x
        screenHeight = screenSize.y
    }

    fun changePlayingState(play: Boolean) {
        if (play) currentVideoHolder?.onResume() else currentVideoHolder?.onPause()
    }

    private fun playVideo(targetViewHolder: StoryPlayerEventListener?) {
        if (currentVideoHolder != null && currentVideoHolder == targetViewHolder) return
        try {
            currentVideoHolder?.onPlayCanceled()
            currentVideoHolder = targetViewHolder
            currentVideoHolder?.onPrePlay(playerVideo)
        } catch (throwable: Throwable) {
            // ignore
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun videoViewHolderChanges(): Flow<StoryPlayerEventListener?> {
        return callbackFlow {
            val listener = object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    trySend(dy).isSuccess
                }
            }
            addOnScrollListener(listener)
            awaitClose {
                removeOnScrollListener(listener)
            }
        }
            .buffer(Channel.CONFLATED)
            .mapLatest { getTargetVideoHolder() }
            .flowOn(IO)
    }

    private fun getTargetVideoHolder(): StoryPlayerEventListener? {
        try {
            val position = findCurrentVideoPosition()
            if (position == NO_POSITION) return null
            val viewHolder = findViewHolderForAdapterPosition(position)
            return if (viewHolder is StoryPlayerEventListener) viewHolder else null
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            return null
        }
    }

    fun findCurrentVideoPosition(): Int {
        var result = NO_POSITION
        val linearLayoutManager = layoutManager as LinearLayoutManager
        val firstPosition = linearLayoutManager.findFirstVisibleItemPosition()
        val lastPosition = linearLayoutManager.findLastVisibleItemPosition()
        var percentMax = 0
        for (position in firstPosition..lastPosition) {
            val percent = getVisibleVideoHeight(position, linearLayoutManager)
            if (percentMax < percent) {
                percentMax = percent
                result = position
            }
        }
        return result
    }

    private fun getVisibleVideoHeight(position: Int, linearLayoutManager: LinearLayoutManager): Int {
        val child = linearLayoutManager.findViewByPosition(position) ?: return NO_POSITION
        val location = IntArray(2)
        child.getLocationInWindow(location)
        return if (location[1] < 0) location[1] + videoItemHeight else screenHeight - location[1]
    }
}

fun View.screenSize(): Point {
    val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val point = Point()
    display.getSize(point)
    return point
}