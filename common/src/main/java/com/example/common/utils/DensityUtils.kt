package com.example.common.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.view.WindowManager
import com.example.rocketcat.App

fun dp2px(dpValue: Float): Int {
    return (0.5f + dpValue * Resources.getSystem()
        .displayMetrics.density).toInt()
}

fun px2dp(pxValue: Int): Float {
    return pxValue.toFloat() / Resources.getSystem()
        .displayMetrics.density
}

fun getScreenSize(): Point {
    val windowManager = App.INSTANCE
        .getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val outPoint = Point()
    // 可能有虚拟按键的情况
    display.getRealSize(outPoint)
    return outPoint
}