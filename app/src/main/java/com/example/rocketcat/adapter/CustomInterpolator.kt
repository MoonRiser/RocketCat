package com.example.rocketcat.adapter

import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.PI
import kotlin.math.cos

class CustomInterpolator(private val factor: Float = 1f) : AccelerateDecelerateInterpolator() {

    private var currentValue = 0f

    override fun getInterpolation(input: Float): Float {
        val value = (cos((input + 1) * PI / factor) / 2.0f).toFloat() + 0.5f
        if (input < 0.01f) {
            currentValue = 0f
        }
        return if (value > currentValue) {
            currentValue = value
            value
        } else {
            1f
        }

    }
}