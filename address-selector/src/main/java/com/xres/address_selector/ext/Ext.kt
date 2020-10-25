package com.xres.address_selector.ext

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.material.color.MaterialColors
import com.xres.address_selector.R

/**
 * @Author:         Xres
 * @CreateDate:     2020/5/19 16:53
 * @Description:
 */


/**
 * 比如函数view.margin()需要的传入的参数是18dp对应的px值，那么直接view.margin(18f.dpValue),表明传入的18是dp为单位
 */
@Deprecated("推荐用顶级属性")
fun Float.dpValue(): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    Resources.getSystem().displayMetrics
).toInt()

val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    ).toInt()


val String.color
    get() = Color.parseColor(this)

val String.colorValue
    get() = Color.parseColor(this)

val View.primaryColor
    get() = MaterialColors.getColor(this, R.attr.colorPrimary)

val View.accentColor
    get() = MaterialColors.getColor(this, R.attr.colorAccent)

/**
 * 尾递归函数，其实就是编译的时候优化了一下，用while循环取代递归，减小开销
 * 获取context的activity
 */
tailrec fun Context?.activity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    else -> (this as? ContextWrapper)?.baseContext?.activity()
}
