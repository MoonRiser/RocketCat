package com.example.common.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.constraintlayout.motion.widget.MotionLayout


class MyMotionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr) {

    private var mStartTime = 0L

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {


        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                mStartTime = event.eventTime
                Log.i("xres", "aa")
                false
            }
            MotionEvent.ACTION_UP -> {
                Log.i("xres", "bb")
                val result = if (event.eventTime - mStartTime <= ViewConfiguration.getTapTimeout())
                    false else super.onInterceptTouchEvent(event)
                Log.i("xres", "cc : $result")
                parent.requestDisallowInterceptTouchEvent(false)
                result
            }
            else -> super.onInterceptTouchEvent(event)
        }


    }
}