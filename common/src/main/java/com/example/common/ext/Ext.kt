package com.example.common.ext

import android.graphics.Color
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.example.common.utils.dp2px
import com.example.common.utils.px2dp

/**
 * @Author:         Xres
 * @CreateDate:     2020/5/19 16:53
 * @Description:
 */

fun <T> MutableLiveData<T>.setValueNoMatterWhichThread(value: T) {

    if (isMainThread()) {
        setValue(value)
    } else {
        postValue(value)
    }

}

fun isMainThread() = Looper.myLooper() === Looper.getMainLooper()

/**
 * 比如函数view.margin()需要的传入的参数是18dp对应的px值，那么直接view.margin(18f.dpValue),表明传入的18是dp为单位
 */
fun Float.dpValue(): Int = dp2px(this)


fun Int.pxValue(): Float = px2dp(this)


fun String.colorValue(): Int = Color.parseColor(this)