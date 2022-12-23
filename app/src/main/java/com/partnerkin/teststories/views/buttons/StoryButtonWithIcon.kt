package com.partnerkin.teststories.views.buttons

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity.CENTER_VERTICAL
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import com.partnerkin.teststories.R
import com.partnerkin.teststories.utils.AndroidUtil.Companion.pxFromDp
import kotlin.random.Random

@Suppress("PrivatePropertyName")
class StoryButtonWithIcon : CardView {

    private val ROOT_BACKGROUND_COLOR = Color.parseColor("#373737")
    private val ROOT_PADDING_VERTICALLY = 8f.pxFromDp(context)
    private val ROOT_PADDING_HORIZONTALLY = 16f.pxFromDp(context)
    private val ROOT_RADIUS = 50f.pxFromDp(context).toFloat()

    private val ICON_SIZE = 26f.pxFromDp(context)
    private val ICON_COLOR = Color.WHITE

    private val VALUE_LEFT_MARGIN = 5f.pxFromDp(context)
    private val VALUE_MAX_COUNTER = 999

    private lateinit var linearLayout : LinearLayout
    private lateinit var iconView : ImageView
    private lateinit var valueView : TextView

    private var icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_star_24) !!
    private var value = 0

    constructor(context: Context) : super(context, null) {
        initAllViews()
    }

    constructor(context : Context, attributeSet : AttributeSet) : super(context, attributeSet) {
        initAllViews()
    }

    private fun initAllViews() {
        initRootView()
        createLinearLayout()
        createIconView()
        createValueView()
    }

    private fun initRootView() {
        radius = ROOT_RADIUS
        updateRootPadding(ROOT_PADDING_VERTICALLY, ROOT_PADDING_VERTICALLY)
        setCardBackgroundColor(ROOT_BACKGROUND_COLOR)
        setOnClickListener {
            setValue(getRndInt())
        }
    }

    private fun createLinearLayout() {
        linearLayout = LinearLayout(context).apply {
            layoutParams = LayoutParams(
                MATCH_PARENT,
                WRAP_CONTENT
            )
            orientation = HORIZONTAL
            gravity = CENTER_VERTICAL
        }
        addView(linearLayout)
    }

    private fun createIconView() {
        iconView = ImageView(context).apply {
            layoutParams = LayoutParams(
                ICON_SIZE,
                ICON_SIZE
            )
            setImageDrawable(icon)
            ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ICON_COLOR))
        }
        linearLayout.addView(iconView)
    }

    private fun createValueView() {
        valueView = TextView(context).apply {
            layoutParams = LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT
            ).apply {
                setMargins(
                    VALUE_LEFT_MARGIN,0,0, 0
                )
            }
            textSize = 16f
            setTextColor(Color.WHITE)
            isVisible = false
        }
        linearLayout.addView(valueView)
    }

    fun setValue(value:Int) {
        if (value != 0) {
            val resultValue = if (value > VALUE_MAX_COUNTER) "+$VALUE_MAX_COUNTER" else value.toString()
            valueView.text = resultValue
            valueView.isVisible = true
            updateRootPadding(ROOT_PADDING_VERTICALLY, ROOT_PADDING_HORIZONTALLY)
        }else {
            valueView.isVisible = false
            updateRootPadding(ROOT_PADDING_VERTICALLY, ROOT_PADDING_VERTICALLY)
        }
    }


    fun setIcon(@DrawableRes icon:Int) {
        iconView.setImageResource(icon)
    }

    private fun updateRootPadding(vertical:Int, horizontal:Int) {
        setContentPadding(
            horizontal,
            vertical,
            horizontal,
            vertical
        )
    }

    //test
    private fun getRndInt() : Int {
        return Random.nextInt(0, 1200)
    }
}