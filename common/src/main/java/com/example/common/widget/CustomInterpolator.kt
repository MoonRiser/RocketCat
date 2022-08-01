package com.example.common.widget

import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.PI
import kotlin.math.cos

//在原生AccelerateDecelerateInterpolator增加了因子，当factor为0.8时，动画执行到80%，到达终点
class CustomInterpolator(val factor: Float = 1f) : AccelerateDecelerateInterpolator() {


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