package com.partnerkin.teststories.utils

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.getSystem
import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider


class AndroidUtil {

    companion object {

        fun Float.pxFromDp(context: Context): Int {
            return (this * context.resources.displayMetrics.density).toInt()
        }

        fun View.setCornerRadiusOfView(radius:Float = 30f) {
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view : View, outline : Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, radius)
                }
            }
            clipToOutline = true
        }

        fun Resources.getDeviceWidth(): Int = getSystem().displayMetrics.widthPixels
        fun Resources.getDeviceHeight(): Int = getSystem().displayMetrics.heightPixels
    }

}