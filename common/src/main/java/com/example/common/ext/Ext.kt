package com.example.common.ext

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Looper
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.common.R
import com.example.common.utils.dp2px
import com.example.common.utils.px2dp
import com.google.android.material.color.MaterialColors

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
@Deprecated("推荐用顶级属性")
fun Float.dpValue(): Int = dp2px(this)

val Float.dp
    get() = dp2px(this)

fun Int.pxValue(): Float = px2dp(this)

val String.color
    get() = Color.parseColor(this)

fun String.colorValue(): Int = Color.parseColor(this)

val View.primaryColor
    get() = MaterialColors.getColor(this, R.attr.colorPrimary)

/**
 * 尾递归函数，其实就是编译的时候优化了一下，用while循环取代递归，减小开销
 * 获取context的activity
 */
tailrec fun Context?.activity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    else -> (this as? ContextWrapper)?.baseContext?.activity()
}
