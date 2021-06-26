package com.example.common.widget

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.absoluteValue


class ViewPager2Container @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    enum class Direction {
        LEFT,
        UP,
        RIGHT,
        DOWN,
    }

    private lateinit var viewPager2: ViewPager2
    private var atStart = false
    private var atEnd = false

    private var lastP = PointF()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        children.find { it is ViewPager2 }?.let {
            viewPager2 = it as ViewPager2
        }
        if (!::viewPager2.isInitialized) {
            throw IllegalStateException("The root child of ViewPager2Container must contains a ViewPager2")
        }
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                atStart = position == 0 && positionOffset == 0f
                atEnd = position == ((viewPager2.adapter?.itemCount ?: 0) - 1)
            }
        })

    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val doNotNeedIntercept =
            !viewPager2.isUserInputEnabled || viewPager2.adapter?.itemCount == 0
        if (doNotNeedIntercept) return super.onInterceptTouchEvent(ev)

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastP = PointF(ev.x, ev.y)
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {

                val disX = ev.x - lastP.x
                val disY = ev.y - lastP.y
                lastP = PointF(ev.x, ev.y)

                val direction = when {
                    disX.absoluteValue >= disY.absoluteValue && disX < 0 -> Direction.LEFT
                    disX.absoluteValue >= disY.absoluteValue && disX > 0 -> Direction.RIGHT
                    disY.absoluteValue > disX.absoluteValue && disY > 0 -> Direction.UP
                    disY.absoluteValue > disX.absoluteValue && disY < 0 -> Direction.DOWN
                    else -> Direction.DOWN
                }

                val shouldParentIntercept = when (direction) {
                    Direction.LEFT, Direction.UP -> atEnd
                    Direction.RIGHT, Direction.DOWN -> atStart
                }
                parent.requestDisallowInterceptTouchEvent(!shouldParentIntercept)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(
                false
            )
        }

        return super.onInterceptTouchEvent(ev)
    }


}