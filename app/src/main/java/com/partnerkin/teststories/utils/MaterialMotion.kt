package com.partnerkin.teststories.utils

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionManager
import com.google.android.material.transition.*


//https://github.com/material-components/material-components-android/blob/master/docs/theming/Motion.md
class MaterialMotion {

    fun addFateThroughAnimation(
        container: ViewGroup,
        duration:Long = 200L
    ) {
        val fadeThrough = MaterialFadeThrough().apply {
            setDuration(duration)
        }
        TransitionManager.beginDelayedTransition(container, fadeThrough)
    }

    fun addFadeAnimation(
        container: ViewGroup,
        duration:Long = 200L
    ) {
        val materialFade = MaterialFade().apply {
            this.duration = duration
        }
        TransitionManager.beginDelayedTransition(container, materialFade)
    }

    fun addAxisAnimation(
        container: ViewGroup,
        axis : Int = MaterialSharedAxis.Z ,
        duration:Long = 300L
    ) {
        val sharedAxis = MaterialSharedAxis(axis, true).apply {
            setDuration(duration)
        }
        TransitionManager.beginDelayedTransition(container, sharedAxis)
    }

    fun addContainerTransformAnimation(
        startView : View,
        endView : View,
        container : ViewGroup
    ) {
        val transform = MaterialContainerTransform().apply {
            this.startView = startView
            this.endView = endView
            setPathMotion(MaterialArcMotion())
            scrimColor = Color.TRANSPARENT
        }
        TransitionManager.beginDelayedTransition(container, transform)
    }


}