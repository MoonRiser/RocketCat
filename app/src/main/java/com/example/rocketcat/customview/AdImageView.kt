package com.example.rocketcat.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView

class AdImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "AdImageView"
    }


    //视觉所能看到的实际高度
    private var minHeight: Int = 0

    //图片在内存中的高度
    private var realHeight: Int = 0


    private var offset: Float = 1f

    init {
        scaleType = ScaleType.CENTER_CROP
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (drawable == null) return
        minHeight = h
        //获取图片的真实比例，然后按照设定的宽，获得高度
        realHeight = (w.toFloat() / drawable.intrinsicWidth * drawable.intrinsicHeight).toInt()
        drawable.setBounds(0, 0, w, realHeight)
    }

    override fun onDraw(canvas: Canvas) {

        canvas.save()
        val dy = (offset - 0.5f) * (minHeight - realHeight)
        Log.i(TAG, "dy: $dy")
        canvas.translate(0f, dy)
        super.onDraw(canvas)
        canvas.restore()

    }


    /**
     * @param parentHeight 父布局的高度
     * @param topToParent 当前布局顶部离父布局顶部的距离
     */
    fun setOffset(parentHeight: Int, topToParent: Int) {

        val temp = topToParent / (parentHeight - minHeight).toFloat()
        if (temp in 0F..1F) {
            offset = temp
            invalidate()
        }


    }

    fun reset() {
        offset = 1f
    }


}