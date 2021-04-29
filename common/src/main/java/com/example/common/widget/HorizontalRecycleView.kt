package com.example.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.absoluteValue

/**
 * @Author:         Xres
 * @CreateDate:     2021/4/29 17:08
 * @Description:
 */
class HorizontalRecycleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RecyclerView(context, attrs, defStyleAttr) {


    private var directionX = 0f
    private var dy = 0f

    private var lastX = 0f
    private var lastY = 0f


    override fun dispatchTouchEvent(e: MotionEvent): Boolean {
        val result = super.dispatchTouchEvent(e)
        if (dy.absoluteValue > ViewConfiguration.get(context).scaledTouchSlop && dy.absoluteValue > directionX.absoluteValue) {
            parent.requestDisallowInterceptTouchEvent(false)
            dy = 0f
            return result
        }


        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = e.x
                lastY = e.y
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                dy = e.y - lastY
                lastY = e.y
                directionX = e.x - lastX
                lastX = e.x
                if (scrollRight() && isScroll2End() || scrollLeft() && isScroll2Start()) {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
                dy = 0f
            }

        }
        return result
    }

//    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
//
//        val result = super.onInterceptTouchEvent(e)
//
//        if (dy.absoluteValue > ViewConfiguration.get(context).scaledTouchSlop && dy.absoluteValue > directionX.absoluteValue) {
//            parent.requestDisallowInterceptTouchEvent(false)
//            dy = 0f
//            return result
//        }
//
//
//        when (e.action) {
//            MotionEvent.ACTION_DOWN -> {
//                lastX = e.x
//                lastY = e.y
//                parent.requestDisallowInterceptTouchEvent(true)
//            }
//            MotionEvent.ACTION_MOVE -> {
//                dy = e.y - lastY
//                lastY = e.y
//                directionX = e.x - lastX
//                lastX = e.x
//                if (scrollRight() && isScroll2End() || scrollLeft() && isScroll2Start()) {
//                    parent.requestDisallowInterceptTouchEvent(false)
//                }
//            }
//            MotionEvent.ACTION_UP -> {
//                parent.requestDisallowInterceptTouchEvent(false)
//                dy = 0f
//            }
//
//        }
//        return result
//
//    }


    private fun scrollLeft(): Boolean = directionX > 0

    private fun scrollRight(): Boolean = directionX < 0


    private fun isScroll2End(): Boolean =
        (layoutManager as? LinearLayoutManager)?.findLastCompletelyVisibleItemPosition()?.let {
            adapter?.itemCount == it + 1
        } ?: false

    private fun isScroll2Start(): Boolean =
        (layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition()?.let {
            it == 0
        } ?: false

}