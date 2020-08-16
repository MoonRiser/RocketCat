package com.example.rocketcat.adapter

import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.PI
import kotlin.math.cos

class CustomInterpolator(private val factor: Float = 1f) : AccelerateDecelerateInterpolator() {


    override fun getInterpolation(input: Float): Float {
        return (cos((input + 1) * PI / factor) / 2.0f).toFloat() + 0.5f
    }
}