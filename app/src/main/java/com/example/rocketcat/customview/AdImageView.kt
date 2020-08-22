package com.example.rocketcat.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.absoluteValue

class AdImageView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {


    //视觉所能看到的实际高度
    private var minHeight: Int = 0

    //图片在内存中的高度
    private var realHeight: Int = 0


    private var offset: Float = 1f
    private lateinit var myDrawable: Drawable


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        minHeight = h
        //获取图片的真实比例，然后按照设定的宽，获得高度
        realHeight = (width * 1.0f / drawable.intrinsicWidth * drawable.intrinsicHeight).toInt()
        myDrawable = drawable.apply {
            setBounds(0, 0, w, realHeight)
        }

    }

    override fun onDraw(canvas: Canvas) {

        canvas.save()
        canvas.translate(0f, (offset - 0.5f) * (minHeight - realHeight))
        super.onDraw(canvas)
        canvas.restore()

    }


    /**
     * @param parentHeight 父布局的高度
     * @param topToParent 当前布局顶部离父布局顶部的距离
     */
    fun setOffset(parentHeight: Int, topToParent: Int) {

        val temp = topToParent / (parentHeight - minHeight).toFloat()
        if (temp.absoluteValue <= 1) {
            this.offset = temp
            invalidate()
        }


    }


}