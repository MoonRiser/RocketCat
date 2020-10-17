package com.example.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.common.ext.color
import com.example.common.ext.dp
import com.example.common.ext.dpValue
import com.example.common.ext.primaryColor
import com.example.common.widget.address_selector.getPinYinFirstCap

const val BG_COLOR = "#E0E0E0"

class SideBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //不带重复值的，用于展示的首字母数组
    private val characters = arrayListOf<String>()

    //带有重复值的首字母数组
    private val rawDataList = arrayListOf<String>()
    private val capIndexMap = mutableMapOf<String, Int>()

    private var selectedIndex = -1
    var mLayoutManager: LinearLayoutManager? = null
    lateinit var mSlideListener: OnSlideListener

    private val charHeight = 20f.dp
    private val paddingV = 8f.dp
    private val roundBackground = GradientDrawable()
    private val textPaint = Paint()
    private var baseLine = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val mWidth = when (wMode) {
            MeasureSpec.AT_MOST -> {
                charHeight
            }
            MeasureSpec.EXACTLY -> {
                wSize.coerceAtLeast(charHeight)
            }
            else -> {
                0
            }

        }

        val hSize = MeasureSpec.getSize(heightMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        val mHeight = when (hMode) {
            MeasureSpec.AT_MOST -> {
                characters.size * charHeight
            }
            MeasureSpec.EXACTLY -> {
                characters.size * charHeight.coerceAtMost(hSize)
            }
            else -> {
                0
            }

        }
        setMeasuredDimension(mWidth, mHeight + 2 * paddingV)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setPadding(0, paddingV, 0, paddingV)
        roundBackground.apply {
            setColor(BG_COLOR.color)
            setBounds(0, 0, w, h)
            cornerRadius = 16f.dp.toFloat()
        }
        textPaint.apply {
            isAntiAlias = true
            textSize = 14f.dp.toFloat()
            textAlign = Paint.Align.CENTER
            color = Color.BLACK
        }.also {
            baseLine = it.fontMetrics.run { (bottom - top) / 2 - bottom }
        }
        mSlideListener = object : OnSlideListener {
            override fun onSlide(index: Int, finished: Boolean) {
                capIndexMap[characters[index]]?.let {
                    mLayoutManager?.scrollToPositionWithOffset(
                        it,
                        0
                    )
                }
            }
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        roundBackground.draw(canvas)
        var centerY = paddingV + charHeight / 2f
        characters.forEachIndexed { index, s ->
            if (selectedIndex == index) {
                textPaint.color = primaryColor
            } else {
                textPaint.color = Color.BLACK
            }
            canvas.drawText(s, width / 2f, centerY + baseLine, textPaint)
            centerY += charHeight
        }


    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val index: Int =
            ((event.y - paddingV) / charHeight).toInt().coerceAtMost(characters.size)
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                mSlideListener.onSlide(index, false)
            }
            MotionEvent.ACTION_UP -> {
                mSlideListener.onSlide(index, true)
                selectedIndex = index
                invalidate()
            }

        }
        return true
    }

    fun setRawList(list: List<String>) {
        rawDataList.apply {
            clear()
            addAll(list.map { it.getPinYinFirstCap() })
        }
        computeCapIndex()
        requestLayout()
    }


    /**
     * 计算需要显示的首字母索引
     */
    private fun computeCapIndex() {
        characters.clear()
        var current = ""
        rawDataList.forEachIndexed { index, s ->
            if (s != current) {
                characters.add(s)
                capIndexMap[s] = index
                current = s
            }
        }

    }
}

interface OnSlideListener {
    fun onSlide(index: Int, finished: Boolean)
}
