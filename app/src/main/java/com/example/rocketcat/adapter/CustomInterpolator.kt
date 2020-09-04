package com.example.rocketcat.adapter

import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.PI
import kotlin.math.cos

//在原生AccelerateDecelerateInterpolator增加了因子，当factor为0.8时，动画执行到80%，到达终点
class CustomInterpolator(private val factor: Float = 1f) : AccelerateDecelerateInterpolator() {

    private var overlap = false

    override fun getInterpolation(input: Float): Float {
        val value = (cos((input + 1) * PI / factor) / 2.0f).toFloat() + 0.5f
        if (value > 0.99f) {
            overlap = true
        }
        if (input == 1f) {
            overlap = false
        }
        return if (overlap) {
            1f
        } else {
            value
        }
    }
}