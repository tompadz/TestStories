package com.partnerkin.teststories.views.buttons

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.partnerkin.teststories.utils.AndroidUtil.Companion.pxFromDp

@Suppress("PrivatePropertyName")
class StoryButtonWithText : CardView {

    private val ROOT_BACKGROUND_COLOR = Color.parseColor("#373737")
    private val ROOT_PADDING_VERTICALLY = 5f.pxFromDp(context)
    private val ROOT_PADDING_HORIZONTALLY = 15f.pxFromDp(context)
    private val ROOT_RADIUS = 50f.pxFromDp(context).toFloat()

    private lateinit var textView : TextView

    constructor(context: Context) : super(context, null) {
        initAllViews()
    }

    constructor(context : Context, attributeSet : AttributeSet) : super(context, attributeSet) {
        initAllViews()
    }

    private fun initAllViews() {
        initRootView()
        createTextView()
    }

    private fun initRootView() {
        radius = ROOT_RADIUS
        updateRootPadding(ROOT_PADDING_VERTICALLY, ROOT_PADDING_HORIZONTALLY)
        setCardBackgroundColor(ROOT_BACKGROUND_COLOR)
    }

    private fun createTextView() {
        textView = TextView(context).apply {
            layoutParams = LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT,
                Gravity.CENTER_VERTICAL
            )
            textSize = 16f
            alpha = 0.6f
            setTextColor(Color.WHITE)
        }
        addView(textView)
    }

    fun setText(text:String) {
        textView.text = text
    }

    private fun updateRootPadding(vertical:Int, horizontal:Int) {
        setContentPadding(
            horizontal,
            vertical,
            horizontal,
            vertical
        )
    }
}