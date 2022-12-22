package com.partnerkin.teststories.views.progress_bar

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.partnerkin.teststories.views.listeners.ProgressListener


@SuppressLint("ViewConstructor")
class StoriesProgressView(
    context : Context,
) : LinearLayout(context, null) {

    private val TAG = "StoriesProgressView"
    private val PROGRESS_BAR_LAYOUT_PARAM = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
    private val SPACE_LAYOUT_PARAM = LayoutParams(5, LayoutParams.WRAP_CONTENT)

    val progressBars : MutableList<StoryProgressBar> = ArrayList()

    private var _storiesCount = 0
    val storiesCount get() = _storiesCount

    private var _currentStoryIndex = 0
    val currentStoryIndex get() = _currentStoryIndex

    private var listener : ProgressListener? = null

    private var _isComplete = false
    val isComplete : Boolean get() = _isComplete

    private var _isStart = false
    val isStart:Boolean get() = _isStart

    private var _isPause = false
    val isPause: Boolean get() = _isPause

    private var isSkipStart = false
    private var isReverseStart = false

    init {
        orientation = HORIZONTAL
        bindViews()
    }

    private fun bindViews() {
        progressBars.clear()
        removeAllViews()
        for (i in 0 until _storiesCount) {
            val p : StoryProgressBar = createProgressBar()
            progressBars.add(p)
            addView(p)
            if (i + 1 < _storiesCount) {
                addView(createSpace())
            }
        }
    }

    private fun createProgressBar() : StoryProgressBar {
        val p = StoryProgressBar(context)
        p.layoutParams = PROGRESS_BAR_LAYOUT_PARAM
        return p
    }

    private fun createSpace() : View {
        val v = View(context)
        v.layoutParams = SPACE_LAYOUT_PARAM
        return v
    }

    fun setStoriesCount(storiesCount : Int) {
        this._storiesCount = storiesCount
        bindViews()
    }

    fun setProgressListener(storiesListener : ProgressListener?) {
        listener = storiesListener
    }

    fun skip() {
        if (isSkipStart || isReverseStart) return
        if (isComplete) return
        if (_currentStoryIndex < 0) return
        val p : StoryProgressBar = progressBars[_currentStoryIndex]
        isSkipStart = true
        _isStart = false
        _isPause = false
        p.setMax()
    }

    fun reverse() {
        if (isSkipStart || isReverseStart) return
        if (isComplete) return
        if (_currentStoryIndex < 0) return
        val p : StoryProgressBar = progressBars[_currentStoryIndex]
        isReverseStart = true
        _isStart = false
        _isPause = false
        p.setMin()
    }


    fun setCurrentStoryDuration(duration : Long, index:Int) {
        progressBars[index].setDuration(duration)
        progressBars[index].setCallback(callback(index))
    }

    fun startStories(from : Int) {
        for (i in 0 until from) {
            progressBars[i].setMaxWithoutCallback()
        }
        progressBars[from].startProgress()
        _isPause = false
        _isStart = true
        _isComplete = false
        isSkipStart = false
        isReverseStart = false
    }

    fun destroy() {
        for (p in progressBars) {
            p.clear()
        }
        _isPause = false
        _isStart = false
    }

    fun pause() {
        if (_currentStoryIndex < 0) return
        progressBars[_currentStoryIndex].pauseProgress()
        _isPause = true
    }

    fun resume() {
        if (_currentStoryIndex < 0) return
        progressBars[_currentStoryIndex].resumeProgress()
        _isPause = false
    }

    private fun callback(index : Int) : StoryProgressBar.Callback {
        return object : StoryProgressBar.Callback {

            override fun onStartProgress() {
                _currentStoryIndex = index
            }

            override fun onFinishProgress() {
                if (isReverseStart) {
                    listener?.onPrev()
                    isReverseStart = false
                    return
                }
                val next : Int = _currentStoryIndex + 1
                if (next <= progressBars.size - 1) {
                    _isStart = false
                    _isPause = false
                    listener?.onNext()
                }else {
                    _isComplete = true
                    _isStart = false
                    _isPause = false
                    listener?.onComplete()
                    destroy()
                }
                isSkipStart = false
                isReverseStart = false
            }
        }
    }
}