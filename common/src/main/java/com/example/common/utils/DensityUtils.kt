package com.example.common.utils

import android.content.res.Resources

fun dp2px(dpValue: Float): Int {
    return (0.5f + dpValue * Resources.getSystem()
        .displayMetrics.density).toInt()
}

fun px2dp(pxValue: Int): Float {
    return pxValue.toFloat() / Resources.getSystem()
        .displayMetrics.density
}