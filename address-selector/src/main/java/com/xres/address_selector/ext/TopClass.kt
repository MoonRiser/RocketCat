package com.xres.address_selector.ext

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import android.widget.Toast


fun showToast(context: Context,msg:String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

//fun getScreenSize(context: Context): Point {
//    val display = context.display
//    val outPoint = Point()
//    // 可能有虚拟按键的情况
//    display?.getRealSize(outPoint)
//    return outPoint
//}

fun getScreenSize(context: Context): Point {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val outPoint = Point()
    // 可能有虚拟按键的情况
    display.getRealSize(outPoint)
    return outPoint
}