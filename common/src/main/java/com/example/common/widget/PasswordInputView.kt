package com.example.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.InputFilter
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.common.ext.dp
import com.example.common.ext.primaryColor
import com.example.common.utils.SoftKeyboardUtil

/**
 * @author xres
 * @createDate 10/28/20
 */

class PasswordInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {


    private val dotRadius = 6f.dp.toFloat()
    private val recStrokeWidth = 1f.dp.toFloat()
    private var recWidth = 16f.dp
    private val recHeight = 48f.dp
    private val recRoundCorner = 4f.dp.toFloat()
    private val spacing = 8f.dp.toFloat()

    private var currentIndex = -1
    private val selectedColor = primaryColor
    private val maxCount = 6

    private var centerY = 0f
    private val dotPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val mPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        strokeWidth = recStrokeWidth
        style = Paint.Style.STROKE
    }

    private val rect = RectF()

    init {
        isFocusableInTouchMode = true
        isFocusable = true
        isCursorVisible = false
        filters = arrayOf(InputFilter.LengthFilter(maxCount))
        setOnClickListener {
            SoftKeyboardUtil.toggleSoftInput(this)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        recWidth = ((w / 2 - 3.5 * spacing) / 3).toInt()
        centerY = h / 2f
    }

    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)

        for (i in 0 until maxCount) {
            val offsetRect = spacing + i * (recWidth + spacing)
            val offsetDot = spacing + recWidth / 2 + i * (recWidth + spacing)
            rect.set(
                offsetRect,
                centerY - recHeight / 2,
                offsetRect + recWidth,
                centerY + recHeight / 2
            )
            mPaint.color = if (i == currentIndex) selectedColor else Color.BLACK
            dotPaint.color = mPaint.color
            canvas.drawRoundRect(rect, recRoundCorner, recRoundCorner, mPaint)
            if (i <= currentIndex && text != null) {
                canvas.drawCircle(offsetDot, centerY, dotRadius, dotPaint)
            }
        }


    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (selStart == selEnd) {
            text?.length?.let {
                setSelection(it)
                currentIndex = it - 1
            }

        }
        invalidate()


    }

    fun setClearContent(clear: Boolean?) {
        if (clear == true)
            text = null
    }
}