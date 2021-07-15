package com.example.rocketcat.customview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import com.example.rocketcat.R

/**
 * @Author:         Xres
 * @CreateDate:     2021/4/8 16:45
 * @Description:
 */
class MyFlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private lateinit var lastView: View
    private var rowCount = 0
    var lineSpacing = 0
    var itemSpacing = 0
    var singleLine = false
    private var MAX_ROWS_COUNT = 3
    private var enableLastView = true

    fun setMaxRows(count: Int) {
        MAX_ROWS_COUNT = count
        requestLayout()
    }

    fun setLastView(view: View) {
        lastView = view
        super.addView(lastView)
    }

    fun enableLastView(enable: Boolean) {
        enableLastView = enable
        requestLayout()
    }

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MyFlowLayout, defStyleAttr, 0)
        enableLastView = array.getBoolean(R.styleable.MyFlowLayout_enableLastView, true)
        array.recycle()
    }

    override fun addView(child: View?) {
        val position = if (childCount > 0) childCount - 1 else 0
        super.addView(child, position)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!::lastView.isInitialized && enableLastView) {
            throw RuntimeException("enableLastView默认为true，请先调用setLastView。或者enableLastView设置为false")
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        val height = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val maxWidth =
            if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) width else Int.MAX_VALUE

        var childLeft = paddingLeft
        var childTop = paddingTop
        var childBottom = childTop
        var maxBottom = 0
        var childRight: Int
        var maxChildRight = 0
        val maxRight = maxWidth - paddingRight
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child.layoutParams
            var leftMargin = 0
            var rightMargin = 0
            if (lp is MarginLayoutParams) {
                leftMargin += lp.leftMargin
                rightMargin += lp.rightMargin
            }
            childRight = childLeft + leftMargin + child.measuredWidth

            // If the current child's right bound exceeds Flowlayout's max right bound and flowlayout is
            // not confined to a single line, move this child to the next line and reset its left bound to
            // flowlayout's left bound.
            if (childRight > maxRight && !singleLine) {
                childLeft = paddingLeft
                childTop = childBottom + lineSpacing
            }
            childRight = childLeft + leftMargin + child.measuredWidth
            childBottom = childTop + child.measuredHeight
            if (childBottom > maxBottom) {
                maxBottom = childBottom
            }
            Log.i("cxk", "childBottom: $childBottom #tag:${child.tag}#maxBottom:$maxBottom")

            // Updates Flowlayout's max right bound if current child's right bound exceeds it.
            if (childRight > maxChildRight) {
                maxChildRight = childRight
            }
            childLeft += leftMargin + rightMargin + child.measuredWidth + itemSpacing

            // For all preceding children, the child's right margin is taken into account in the next
            // child's left bound (childLeft). However, childLeft is ignored after the last child so the
            // last child's right margin needs to be explicitly added to Flowlayout's max right bound.
            if (i == childCount - 1) {
                maxChildRight += rightMargin
            }

        }

        maxChildRight += paddingRight
        maxBottom += paddingBottom

        val finalWidth = getMeasuredDimension(width, widthMode, maxChildRight)
        val finalHeight = getMeasuredDimension(height, heightMode, maxBottom)
        setMeasuredDimension(finalWidth, finalHeight)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        if (childCount == 0) {
            rowCount = 0
            return
        }

        val maxWidth = r - l
        rowCount = 1
        var alreadyFull = false

        val isRtl = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
        val paddingStart = if (isRtl) paddingRight else paddingLeft
        val paddingEnd = if (isRtl) paddingLeft else paddingRight
        var childStart = paddingStart
        var childTop = paddingTop
        var childBottom = childTop
        var childEnd: Int
        val maxChildEnd = right - left - paddingEnd
        var maxBottom = childBottom

        children.toList().forEachIndexed { i, child ->
            if (child.visibility == GONE) {
//                child.setTag(R.id.row_index_key, -1)
                return@forEachIndexed
            }
            if (alreadyFull) {
                if (!enableLastView) {
                    removeViewInLayout(child)
                    return@forEachIndexed
                }
                if (child != lastView) {
                    removeViewInLayout(child)
                    return@forEachIndexed
                }
            }
            val lp = child.layoutParams
            var startMargin = 0
            var endMargin = 0
            if (lp is MarginLayoutParams) {
                startMargin = MarginLayoutParamsCompat.getMarginStart(lp)
                endMargin = MarginLayoutParamsCompat.getMarginEnd(lp)
            }
            childEnd = childStart + startMargin + child.measuredWidth
//********************************
            if (rowCount == MAX_ROWS_COUNT && !singleLine && enableLastView) {
                if (child == lastView) {
                    child.layout(
                        childStart + startMargin,
                        maxBottom - lastView.measuredHeight,
                        childEnd,
                        maxBottom
                    )
                    return
                }
                if (childStart + lastView.measuredWidth > maxChildEnd) {
                    val pre = getChildAt(i - 1)
                    removeViewInLayout(pre)
                    alreadyFull = true
                    return@forEachIndexed
                } else {
                    if (childEnd + lastView.measuredWidth > maxWidth) {
                        removeViewInLayout(child)
                        alreadyFull = true
                        return@forEachIndexed
                    }
                }
            }
//********************************
            if (!singleLine && childEnd > maxChildEnd) {
                childStart = paddingStart
                childTop = childBottom + lineSpacing
                rowCount++
                if (!enableLastView && rowCount > MAX_ROWS_COUNT) {
                    alreadyFull = true
                    removeViewInLayout(child)
                    return@forEachIndexed
                }
            }
//            child.setTag(R.id.row_index_key, rowCount - 1)
            childEnd = childStart + startMargin + child.measuredWidth
            childBottom = childTop + child.measuredHeight
            if (childBottom > maxBottom) {
                maxBottom = childBottom
            }
            if (isRtl) {
                child.layout(
                    maxChildEnd - childEnd,
                    childTop,
                    maxChildEnd - childStart - startMargin,
                    childBottom
                )
            } else {
                child.layout(childStart + startMargin, childTop, childEnd, childBottom)
            }
            childStart += startMargin + endMargin + child.measuredWidth + itemSpacing
        }


    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MyLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MyLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MyLayoutParams(context, attrs)
    }

    class MyLayoutParams : MarginLayoutParams {
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(p: LayoutParams?) : super(p)

    }

    companion object {

        private fun getMeasuredDimension(size: Int, mode: Int, childrenEdge: Int): Int {
            return when (mode) {
                MeasureSpec.EXACTLY -> size
                MeasureSpec.AT_MOST -> childrenEdge.coerceAtMost(size)
                else -> childrenEdge
            }
        }
    }


}