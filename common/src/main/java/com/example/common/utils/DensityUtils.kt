package com.example.common.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.TypedValue
import android.view.WindowManager

fun dp2px(dpValue: Float) =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpValue,
        Resources.getSystem().displayMetrics
    ).toInt()


fun px2dp(pxValue: Int): Float {
    return pxValue.toFloat() / Resources.getSystem()
        .displayMetrics.density
}

fun getScreenSize(context: Context): Point {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val outPoint = Point()
    // 可能有虚拟按键的情况
    display.getRealSize(outPoint)
    return outPoint
}